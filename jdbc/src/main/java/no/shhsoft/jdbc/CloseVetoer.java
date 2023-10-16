package no.shhsoft.jdbc;

import java.sql.Connection;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface CloseVetoer {

    boolean shouldClose(Connection connection);

}
