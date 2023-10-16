package no.shhsoft.classpath;

import no.shhsoft.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Sun has decided that this class <strong>must</strong> be named <code>Handler</code>,
 * and that the last element of the package name must be the same as the URL protocol
 * being handled.
 *
 * This handler will register itself once this class is loaded.  Make sure you
 * force it to load somehow (eg. using <code>Class.forName</code>) before relying
 * on it to handle your URLs.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Handler
extends URLStreamHandler {

    private static final String PROTOCOL_HANDLERS_PROP_NAME = "java.protocol.handler.pkgs";

    static {
        /* register handler. */
        String protocolHandlers = System.getProperty(PROTOCOL_HANDLERS_PROP_NAME);
        if (protocolHandlers == null) {
            protocolHandlers = "";
        }
        if (protocolHandlers.length() > 0) {
            protocolHandlers = "|" + protocolHandlers;
        }
        protocolHandlers = "no.shhsoft" + protocolHandlers;
        System.setProperty(PROTOCOL_HANDLERS_PROP_NAME, protocolHandlers);
    }

    @Override
    protected URLConnection openConnection(final URL u)
    throws IOException {
        return new URLConnection(u) {
            @Override
            public void connect()
            throws IOException {
            }

            @Override
            public InputStream getInputStream()
            throws IOException {
                return IoUtils.openResource(u.getPath());
            }
        };
    }

}
