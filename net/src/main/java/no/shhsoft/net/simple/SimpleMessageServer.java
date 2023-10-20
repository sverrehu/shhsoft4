package no.shhsoft.net.simple;

import no.shhsoft.net.MessageHandler;
import no.shhsoft.thread.DaemonThread;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class SimpleMessageServer
extends DaemonThread
implements ClientDisconnectListener {

    private static final Logger LOG = Logger.getLogger(SimpleMessageServer.class.getName());
    private final InetSocketAddress address;
    private final MessageHandler messageHandler;
    private ServerSocket listenerSocket;
    private final Set<ClientProxy> clientProxies = new HashSet<>();

    private static String getAddressString(final InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ':' + address.getPort();
    }

    private static String getAddressString(final Socket socket) {
        return getAddressString((InetSocketAddress) socket.getRemoteSocketAddress());
    }

    @Override
    protected synchronized void beforeStart() {
        try {
            listenerSocket = new ServerSocket();
            listenerSocket.setReuseAddress(true);
            listenerSocket.bind(address);
        } catch (final IOException e) {
            throw new UncheckedIoException("Unable to initiate listening socket: " + e.getMessage(), e);
        }
    }

    @Override
    protected synchronized void afterStop() {
        if (listenerSocket != null) {
            IoUtils.closeSilently(listenerSocket);
            listenerSocket = null;
        }
        for (final ClientProxy clientProxy : clientProxies) {
            clientProxy.stop();
        }
        clientProxies.clear();
    }

    public SimpleMessageServer(final InetSocketAddress address, final MessageHandler messageHandler) {
        this.address = address;
        this.messageHandler = messageHandler;
        setVmShouldWaitForThisThread(true);
    }

    @Override
    public void notifyClientDisconnect(final ClientProxy clientProxy) {
        LOG.info("Disconnect from " + getAddressString(clientProxy.getSocket()));
        clientProxies.remove(clientProxy);
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            try {
                final Socket clientSocket = listenerSocket.accept();
                LOG.info("Connect from " + getAddressString(clientSocket));
                try {
                    clientSocket.setTcpNoDelay(true);
                } catch (final SocketException e) {
                    LOG.fine("Unable to setTcpNoDelay.  Ignored.");
                }
                final ClientProxy clientProxy = new ClientProxy(clientSocket, messageHandler, this);
                clientProxy.start();
                synchronized (this) {
                    clientProxies.add(clientProxy);
                }
            } catch (final IOException e) {
                if (!shouldStop()) {
                    LOG.warning("Server: accept failed: " + e.getMessage());
                }
                continue;
            }
        }
    }

}
