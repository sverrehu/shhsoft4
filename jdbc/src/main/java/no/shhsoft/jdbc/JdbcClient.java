package no.shhsoft.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JdbcClient {

    private static final Logger LOG = Logger.getLogger(JdbcClient.class.getName());
    private DataSource dataSource;
    private boolean logQueries;

    private Connection getConnection()
    throws SQLException {
        return dataSource.getConnection();
    }

    private void rollbackAfterException(final Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (final SQLException e) {
            LOG.log(Level.WARNING, "Rollback after exception failed", e);
        }
    }

    public JdbcClient(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Object executeQuery(final String preparedSql, final PreparedStatementSetter setter, final ResultSetHandler handler) {
        if (logQueries) {
            LOG.info("executeQuery: " + preparedSql);
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(preparedSql);
            if (setter != null) {
                setter.setParameters(stmt);
            }
            rs = stmt.executeQuery();
            Object ret = null;
            if (handler != null) {
                ret = handler.handle(rs);
            }
            return ret;
        } catch (final SQLException e) {
            throw new UncheckedSqlException("Error for query `" + preparedSql + "'", e);
        } finally {
            JdbcUtils.close(conn, stmt, rs);
        }
    }

    public Object executeQuery(final String preparedSql, final ResultSetHandler handler) {
        return executeQuery(preparedSql, null, handler);
    }

    public Object executeQuery(final String preparedSql, final ResultSetHandler handler, final Object... args) {
        return executeQuery(preparedSql, new ArgPreparedStatementSetter(args), handler);
    }

    public int executeUpdate(final String preparedSql, final PreparedStatementSetter setter) {
        if (logQueries) {
            LOG.info("executeUpdate: " + preparedSql);
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(preparedSql);
            if (setter != null) {
                setter.setParameters(stmt);
            }
            return stmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedSqlException("Error for update `" + preparedSql + "'", e);
        } finally {
            JdbcUtils.close(conn, stmt);
        }
    }

    public int executeUpdate(final String preparedSql) {
        return executeUpdate(preparedSql, (PreparedStatementSetter) null);
    }

    public int executeUpdate(final String preparedSql, final Object... args) {
        return executeUpdate(preparedSql, new ArgPreparedStatementSetter(args));
    }

    public Object executeTransaction(final ConnectionUser connUser) {
        boolean setAutoCommitToTrueAtEnd = false;
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn.getAutoCommit()) {
                setAutoCommitToTrueAtEnd = true;
                conn.setAutoCommit(false);
            }
            Object ret = null;
            if (connUser != null) {
                ret = connUser.useConnection(conn);
            }
            conn.commit();
            return ret;
        } catch (final SQLException e) {
            rollbackAfterException(conn);
            throw new UncheckedSqlException(e);
        } catch (final RuntimeException e) {
            rollbackAfterException(conn);
            throw e;
        } finally {
            if (setAutoCommitToTrueAtEnd) {
                try {
                    conn.setAutoCommit(true);
                } catch (final SQLException e) {
                    LOG.log(Level.WARNING, "Resetting auto-commit flag failed", e);
                }
            }
            JdbcUtils.close(conn);
        }
    }

    public void setLogQueries(final boolean logQueries) {
        this.logQueries = logQueries;
    }

}
