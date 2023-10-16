package no.shhsoft.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ArgPreparedStatementSetter
implements PreparedStatementSetter {

    private final Object[] objects;
    private int index;

    private void stmtSet(final int i, final PreparedStatement stmt, final Object object)
    throws SQLException {
        if (object == null) {
            /* This might not work on all databases. (Spring has special handling for Informix.) */
            stmt.setNull(i, Types.NULL);
        } else if (object instanceof CharSequence) {
            stmt.setString(i, ((CharSequence) object).toString());
        } else if (object instanceof Long) {
            stmt.setLong(i, (Long) object);
        } else if (object instanceof Integer) {
            stmt.setInt(i, (Integer) object);
        } else if (object instanceof Date) {
            stmt.setTimestamp(i, new java.sql.Timestamp(((Date) object).getTime()));
        } else if (object instanceof Byte) {
            stmt.setByte(i, (Byte) object);
        } else if (object instanceof Short) {
            stmt.setShort(i, (Short) object);
        } else if (object instanceof Boolean) {
            stmt.setBoolean(i, (Boolean) object);
        } else if (object instanceof Float) {
            stmt.setFloat(i, (Float) object);
        } else if (object instanceof Double) {
            stmt.setDouble(i, (Double) object);
        } else if (object instanceof byte[]) {
            stmt.setBytes(i, (byte[]) object);
        } else {
            throw new RuntimeException("Unhandled type: " + object.getClass().getName());
        }
    }

    public ArgPreparedStatementSetter(final int startIndex, final Object... objects) {
        this.objects = objects;
        this.index = startIndex;
    }

    public ArgPreparedStatementSetter(final Object... objects) {
        this(1, objects);
    }

    public ArgPreparedStatementSetter(final int startIndex, final List<Object> objects) {
        this(startIndex, objects.toArray(new Object[0]));
    }

    public ArgPreparedStatementSetter(final List<Object> objects) {
        this(1, objects);
    }

    @Override
    public void setParameters(final PreparedStatement stmt)
    throws SQLException {
        for (final Object object : objects) {
            stmtSet(index++, stmt, object);
        }
    }

}
