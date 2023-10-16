package no.shhsoft.utils;

import java.io.InputStream;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface ResourceStreamOpener {

    InputStream openResource(String name);

}
