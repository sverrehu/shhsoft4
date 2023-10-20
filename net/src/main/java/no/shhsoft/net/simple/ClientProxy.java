package no.shhsoft.net.simple;

import no.shhsoft.net.Message;
import no.shhsoft.net.MessageHandler;
import no.shhsoft.thread.DaemonThread;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

import java.io.*;
import java.net.Socket;

/**
 * @author <a href="shh@thathost.com">Sverre H. Huseby</a>
 */
final class ClientProxy
extends DaemonThread {

    private final Socket socket;
    private final MessageHandler messageHandler;
    private final ClientDisconnectListener clientDisconnectListener;
    private DataInputStream in;
    private DataOutputStream out;

    private void releaseResources() {
        IoUtils.closeSilently(in);
        IoUtils.closeSilently(out);
        IoUtils.closeSilently(socket);
    }

    private void sendMessage(final Message m)
    throws IOException {
        m.writeToStream(out);
        out.flush();
    }

    private Message receiveMessage() {
        final Message m = new Message();
        m.readFromStream(in);
        return m;
    }

    private void notifyDisconnect() {
        if (clientDisconnectListener != null) {
            clientDisconnectListener.notifyClientDisconnect(this);
        }
    }

    Socket getSocket() {
        return socket;
    }

    public ClientProxy(final Socket socket, final MessageHandler messageHandler,
                       final ClientDisconnectListener clientDisconnectListener) {
        this.socket = socket;
        this.messageHandler = messageHandler;
        this.clientDisconnectListener = clientDisconnectListener;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), 8192));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 8192));
            while (!shouldStop()) {
                final Message request = receiveMessage();
                final Message response = messageHandler.handleMessage(request);
                sendMessage(response);
            }
        } catch (final IOException | UncheckedIoException e) {
            notifyDisconnect();
        } finally {
            releaseResources();
        }
    }

}
