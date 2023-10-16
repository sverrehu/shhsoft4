package no.shhsoft.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface PreparedStatementSetter {

    void setParameters(PreparedStatement stmt)
    throws SQLException;

}
