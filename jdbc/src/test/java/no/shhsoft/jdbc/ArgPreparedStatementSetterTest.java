package no.shhsoft.jdbc;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ArgPreparedStatementSetterTest
extends AbstractJdbcTestBase {

    private static final String TABLE_NAME = ArgPreparedStatementSetterTest.class.getName().replaceAll(".*\\.", "");

    @BeforeClass
    public static void createTable() {
        final DataSource dataSource = getDataSource1();
        final String bigint = JdbcUtils.getTypeName(dataSource, Types.BIGINT);
        final String timestamp = JdbcUtils.getTypeName(dataSource, Types.TIMESTAMP);
        getJdbcClient1().executeUpdate(
            "CREATE TABLE " + TABLE_NAME + "("
            + "bool " + JdbcUtils.getTypeName(dataSource, Types.BOOLEAN) + ", "
            + "byte INTEGER, "
            + "short INTEGER, "
            + "int INTEGER, "
            + "long " + bigint + ", "
            + "float " + JdbcUtils.getTypeName(dataSource, Types.FLOAT) + ", "
            + "double " + JdbcUtils.getTypeName(dataSource, Types.DOUBLE) + ", "
            + "string VARCHAR(80), "
            + "date " + timestamp + ", "
            + "nullString VARCHAR(80))");
    }

    @Test
    public void shouldWriteCorrectly() {
        final Date now = new Date();
        final String fields = "bool, byte, short, int, long, float, double, string, date, nullString";
        final String insertSql = "INSERT INTO " + TABLE_NAME + " "
            + "(" + fields + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        getJdbcClient1().executeUpdate(insertSql,
                                       Boolean.TRUE,
                                       Byte.MAX_VALUE,
                                       Short.MAX_VALUE,
                                       Integer.MAX_VALUE,
                                       Long.MAX_VALUE,
                                       Float.MAX_VALUE,
                                       Double.MAX_VALUE,
                                       "foo",
                                       now,
                                       null);
        final String querySql = "SELECT " + fields + " FROM " + TABLE_NAME;
        getJdbcClient1().executeQuery(querySql, rs -> {
            assertTrue(rs.next());
            int idx = 0;
            assertTrue(rs.getBoolean(++idx));
            assertEquals(Byte.MAX_VALUE, rs.getByte(++idx));
            assertEquals(Short.MAX_VALUE, rs.getShort(++idx));
            assertEquals(Integer.MAX_VALUE, rs.getInt(++idx));
            assertEquals(Long.MAX_VALUE, rs.getLong(++idx));
            assertEquals(Float.MAX_VALUE, rs.getFloat(++idx), 0.1);
            assertEquals(Double.MAX_VALUE, rs.getDouble(++idx), 0.1);
            assertEquals("foo", rs.getString(++idx));
            assertEquals(now, rs.getTimestamp(++idx));
            assertNull(rs.getString(++idx));
            return null;
        });
    }

}
