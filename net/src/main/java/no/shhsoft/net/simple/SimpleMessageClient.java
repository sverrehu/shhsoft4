package no.shhsoft.net.simple;

import no.shhsoft.net.Message;
import no.shhsoft.net.MessageClient;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class SimpleMessageClient
implements MessageClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private void connect(final InetAddress address, final int port) {
        try {
            disconnect();
            socket = new Socket(address, port);
            socket.setTcpNoDelay(true);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (final IOException e) {
            throw new UncheckedIoException(
                "Unable to open connection to " + address + ":" + port, e);
        }
    }

    public void connect(final InetSocketAddress address) {
        connect(address.getAddress(), address.getPort());
    }

    public void disconnect() {
        IoUtils.closeSilently(socket);
    }

    @Override
    public synchronized Message sendMessage(final Message message) {
        message.writeToStream(out);
        final Message response = new Message();
        response.readFromStream(in);
        return response;
    }

}
