package no.shhsoft.net.simple;

import no.shhsoft.net.Message;
import no.shhsoft.net.MessageHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="shh@thathost.com">Sverre H. Huseby</a>
 */
public final class SimpleClientServerTest {

    private static final int PORT = 10103;
    private static SimpleMessageServer server = null;
    private static SimpleMessageClient client;

    protected static final class EchoMessageHandler
    implements MessageHandler {

        @Override
        public Message handleMessage(final Message request) {
            return request;
        }
    }

    private void sendAndReceiveEcho() {
        final String txt = "hello";
        final Message request = new Message();
        request.putString(txt);
        final Message response = client.sendMessage(request);
        final String returnedTxt = response.getString();
        assertEquals(txt, returnedTxt);
    }

    @BeforeClass
    public static void beforeClass()
    throws Exception {
        if (server != null) {
            return;
        }
        final InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), PORT);
        server = new SimpleMessageServer(address, new EchoMessageHandler());
        server.start();

        client = new SimpleMessageClient();
        client.connect(address);
    }

    @AfterClass
    public static void afterClass()
    throws Exception {
        client.disconnect();
        server.stop();
    }

    @Test
    public void shouldSuccessfullySendAndReceiveEcho() {
        sendAndReceiveEcho();
        sendAndReceiveEcho();
    }

}
