package no.shhsoft.jdbc;

import no.shhsoft.utils.AbstractPool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PoolingDataSource
extends AbstractPool<CloseVetoableConnection>
implements DataSource, CloseVetoer {

    private static final Logger LOG = Logger.getLogger(PoolingDataSource.class.getName());
    private String driverClassName;
    private String url;
    private String userName;
    private String password;
    private String validityCheckStatement = "SELECT 1";
    private boolean defaultAutoCommit = true;
    private boolean defaultReadOnly = false;
    private int defaultTransactionIsolation = -1;

    private void assertNoLiveConnections() {
        if (getNumLive() > 0) {
            throw new RuntimeException("Cannot set new attributes when there are live connections.");
        }
    }

    @Override
    protected CloseVetoableConnection create() {
        if (getDriverClassName() == null) {
            throw new RuntimeException("driverClassName must be given");
        }
        if (getUrl() == null) {
            throw new RuntimeException("Database url must be given");
        }
        LOG.info("Creating new database connection for " + getUrl());
        try {
            final Connection connection = DriverManager.getConnection(getUrl(), getUserName(), getPassword());
            return new CloseVetoableConnection(connection, this);
        } catch (final SQLException e) {
            throw new UncheckedSqlException("Error creating JDBC connection for " + getUrl(), e);
        }
    }

    @Override
    protected void destroy(final CloseVetoableConnection connection) {
        LOG.info("Destroying bad database connection for " + getUrl());
        try {
            connection.forceClose();
        } catch (final Throwable t) {
            LOG.info("Ignoring error destroying a Connection: " + t.getMessage());
        }
    }

    @Override
    protected boolean isValid(final CloseVetoableConnection connection) {
        if (getValidityCheckStatement() != null) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.execute(getValidityCheckStatement());
            } catch (final Throwable t) {
                return false;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (final Throwable t) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void prepareForAllocate(final CloseVetoableConnection connection) {
        try {
            connection.setAutoCommit(isDefaultAutoCommit());
            if (getDefaultTransactionIsolation() >= 0) {
                connection.setTransactionIsolation(getDefaultTransactionIsolation());
            }
            connection.setReadOnly(isDefaultReadOnly());
        } catch (final SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    @Override
    public boolean shouldClose(final Connection connection) {
        release((CloseVetoableConnection) connection);
        return false;
    }

    @Override
    public Connection getConnection()
    throws SQLException {
        return allocate();
    }

    @Override
    public Connection getConnection(final String username, final String pwd)
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PrintWriter getLogWriter()
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getLoginTimeout()
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setLogWriter(final PrintWriter out)
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setLoginTimeout(final int seconds)
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface)
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> T unwrap(final Class<T> iface)
    throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Logger getParentLogger()
    throws SQLFeatureNotSupportedException {
        return LOG.getParent();
    }

    public synchronized void setDriverClassName(final String driverClassName) {
        assertNoLiveConnections();
        this.driverClassName = driverClassName;
        try {
            Class.forName(driverClassName);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + driverClassName, e);
        }
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public synchronized void setUrl(final String url) {
        assertNoLiveConnections();
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public synchronized void setUserName(final String userName) {
        assertNoLiveConnections();
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public synchronized void setPassword(final String password) {
        assertNoLiveConnections();
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public synchronized void setValidityCheckStatement(final String validityCheckStatement) {
        assertNoLiveConnections();
        this.validityCheckStatement = validityCheckStatement;
    }

    public String getValidityCheckStatement() {
        return validityCheckStatement;
    }

    public synchronized void setDefaultAutoCommit(final boolean defaultAutoCommit) {
        assertNoLiveConnections();
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public synchronized void setDefaultReadOnly(final boolean defaultReadOnly) {
        assertNoLiveConnections();
        this.defaultReadOnly = defaultReadOnly;
    }

    public boolean isDefaultReadOnly() {
        return defaultReadOnly;
    }

    public synchronized void setDefaultTransactionIsolation(final int defaultTransactionIsolation) {
        assertNoLiveConnections();
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

}
