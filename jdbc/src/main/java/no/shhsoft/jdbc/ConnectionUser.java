package no.shhsoft.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface ConnectionUser {

    /**
     * Caller must not close connection, but is responsible for closing any statements and
     * result sets used.
     */
    Object useConnection(Connection conn)
    throws SQLException;

}
