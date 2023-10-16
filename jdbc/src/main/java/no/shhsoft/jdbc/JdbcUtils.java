package no.shhsoft.jdbc;

import no.shhsoft.validation.Validate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JdbcUtils {

    private static final Logger LOG = Logger.getLogger(JdbcUtils.class.getName());

    private JdbcUtils() {
        /* not to be instantiated. */
    }

    private static void handleCloseThrowable(final Throwable t, final String closeable) {
        if (t instanceof SQLException) {
            LOG.info("Error closing " + closeable + ": " + t.getMessage());
        } else {
            LOG.info("Unexpected exception " + t.getClass().getName()
                     + " when closing " + closeable + ": " + t.getMessage());
        }
    }

    public static void close(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (final Throwable t) {
                handleCloseThrowable(t, "Connection");
            }
        }
    }

    public static void close(final Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (final Throwable t) {
                handleCloseThrowable(t, "Statement");
            }
        }
    }

    public static void close(final ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (final Throwable t) {
                handleCloseThrowable(t, "ResultSet");
            }
        }
    }

    public static void close(final Connection conn, final Statement stmt, final ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }

    public static void close(final Statement stmt, final ResultSet rs) {
        close(rs);
        close(stmt);
    }

    public static void close(final Connection conn, final Statement stmt) {
        close(stmt);
        close(conn);
    }

    public static void close(final Connection conn, final ResultSet rs) {
        close(rs);
        close(conn);
    }

    public static String getTypeName(final DataSource dataSource, final int jdbcType) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            if (metaData.getDriverName().contains("PostgreSQL")) {
                if (jdbcType == Types.LONGVARCHAR || jdbcType == Types.LONGNVARCHAR) {
                    return "text";
                } else if (jdbcType == Types.VARBINARY || jdbcType == Types.LONGVARBINARY) {
                        return "bytea";
                } else if (jdbcType == Types.BIGINT) {
                    return "int8";
                } else if (jdbcType == Types.TIMESTAMP) {
                    return "timestamp";
                } else if (jdbcType == Types.BOOLEAN) {
                    return "boolean";
                }
            } else if (metaData.getDriverName().contains("Oracle")) {
                if (jdbcType == Types.LONGVARCHAR || jdbcType == Types.LONGNVARCHAR) {
                    return "clob";
                }
            } else if (metaData.getDriverName().contains("HSQL")) {
                if (jdbcType == Types.FLOAT) {
                    return "float";
                } else if (jdbcType == Types.REAL) {
                    return "real";
                } else if (jdbcType == Types.LONGVARCHAR) {
                    return "longvarchar";
                }
            }
            rs = metaData.getTypeInfo();
            while (rs.next()) {
                if (rs.getInt("DATA_TYPE") == jdbcType) {
                    //System.out.println("JDBC: " + jdbcType + "=" + rs.getString("TYPE_NAME"));
                    return rs.getString("TYPE_NAME");
                }
            }
            throw new RuntimeException("Type name not found for jdbcType " + jdbcType);
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        } finally {
            JdbcUtils.close(conn, rs);
        }
    }

    public static boolean tableExists(final DataSource dataSource, final String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            String compareName = tableName;
            if (metaData.storesLowerCaseIdentifiers()) {
                compareName = compareName.toLowerCase();
            } else if (metaData.storesUpperCaseIdentifiers()) {
                compareName = compareName.toUpperCase();
            }
            rs = metaData.getTables(null, null, compareName, null);
            return rs.next();
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        } finally {
            JdbcUtils.close(conn, rs);
        }
    }

    public static boolean columnExists(final DataSource dataSource, final String tableName, final String columnName) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            String compareTableName = tableName;
            String compareColumnName = columnName;
            if (metaData.storesLowerCaseIdentifiers()) {
                compareTableName = compareTableName.toLowerCase();
                compareColumnName = compareColumnName.toLowerCase();
            } else if (metaData.storesUpperCaseIdentifiers()) {
                compareTableName = compareTableName.toUpperCase();
                compareColumnName = compareColumnName.toUpperCase();
            }
            rs = metaData.getColumns(null, null, compareTableName, compareColumnName);
            return rs.next();
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        } finally {
            JdbcUtils.close(conn, rs);
        }
    }

    public static void stmtSetLong(final PreparedStatement stmt, final int idx, final Long l) {
        try {
            if (l == null) {
                stmt.setNull(idx, Types.BIGINT);
            } else {
                stmt.setLong(idx, l);
            }
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    public static Long rsGetLong(final ResultSet rs, final int idx) {
        try {
            final long value = rs.getLong(idx);
            if (value == 0L && rs.wasNull()) {
                return null;
            }
            return value;
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    public static void stmtSetTimestamp(final PreparedStatement stmt, final int idx, final Date date) {
        try {
            if (date == null) {
                stmt.setNull(idx, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(idx, new java.sql.Timestamp(date.getTime()));
            }
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    public static String getPlaceholderList(final int n) {
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < n; q++) {
            if (q > 0) {
                sb.append(',');
            }
            sb.append('?');
        }
        return sb.toString();
    }

    public static int count(final JdbcClient jdbcClient, final String tableName) {
        return countWhere(jdbcClient, tableName, null, null);
    }

    public static void addColumn(final JdbcClient jdbcClient, final String tableName, final String columnName, final String sqlTypeSpec) {
        Validate.notNull(jdbcClient);
        Validate.notNull(tableName);
        Validate.notNull(columnName);
        Validate.notNull(sqlTypeSpec);
        jdbcClient.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + sqlTypeSpec);
    }

    public static void dropColumn(final JdbcClient jdbcClient, final String tableName, final String columnName) {
        Validate.notNull(jdbcClient);
        Validate.notNull(tableName);
        Validate.notNull(columnName);
        jdbcClient.executeUpdate("ALTER TABLE " + tableName + " DROP COLUMN " + columnName );
    }

    public static void renameColumn(final JdbcClient jdbcClient, final String tableName, final String oldColumnName, final String newColumnName) {
        Validate.notNull(jdbcClient);
        Validate.notNull(tableName);
        Validate.notNull(oldColumnName);
        Validate.notNull(newColumnName);
        jdbcClient.executeUpdate("ALTER TABLE " + tableName + " RENAME COLUMN " + oldColumnName + " TO " + newColumnName);
    }

    public static void changeColumnType(final JdbcClient jdbcClient, final String tableName, final String columnName, final String newSqlTypeSpec) {
        Validate.notNull(jdbcClient);
        Validate.notNull(tableName);
        Validate.notNull(columnName);
        Validate.notNull(newSqlTypeSpec);
        jdbcClient.executeUpdate("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE " + newSqlTypeSpec);
    }

    public static void createIndex(final JdbcClient jdbcClient, final String tableName, final String columnName) {
        Validate.notNull(jdbcClient);
        Validate.notNull(tableName);
        Validate.notNull(columnName);
        jdbcClient.executeUpdate("CREATE INDEX " + tableName + "_" + columnName + "_idx " + "ON " + tableName + "(" + columnName + ")");
    }

    public static int countWhere(final JdbcClient jdbcClient, final String tableName, final String condition) {
        return countWhere(jdbcClient, tableName, condition, null);
    }

    public static int countWhere(final JdbcClient jdbcClient, final String tableName, final String conditionWithPlaceholders, final PreparedStatementSetter setter) {
        Validate.notNull(jdbcClient);
        Validate.notNull(tableName);
        String sql = "SELECT COUNT(*) FROM " + tableName;
        if (conditionWithPlaceholders != null) {
            sql += " WHERE " + conditionWithPlaceholders;
        }
        return (Integer) jdbcClient.executeQuery(sql, setter, ResultSetHandler.SINGLE_INTEGER_RESULT_SET_HANDLER);
    }


}
