package no.shhsoft.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface ResultSetHandler {

    ResultSetHandler SINGLE_INTEGER_RESULT_SET_HANDLER = rs -> {
        if (!rs.next()) {
            throw new RuntimeException("No integer result");
        }
        return rs.getInt(1);
    };

    Object handle(ResultSet rs)
    throws SQLException;

}
