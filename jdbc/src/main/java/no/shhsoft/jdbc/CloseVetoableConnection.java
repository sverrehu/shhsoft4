package no.shhsoft.jdbc;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CloseVetoableConnection
implements Connection {

    private final Connection wrappedConnection;
    private final CloseVetoer closeVetoer;

    public CloseVetoableConnection(final Connection wrappedConnection, final CloseVetoer closeVetoer) {
        this.wrappedConnection = wrappedConnection;
        this.closeVetoer = closeVetoer;
    }

    @Override
    public void clearWarnings()
    throws SQLException {
        wrappedConnection.clearWarnings();
    }

    @Override
    public void close()
    throws SQLException {
        if (closeVetoer.shouldClose(this)) {
            wrappedConnection.close();
        }
    }

    public void forceClose()
    throws SQLException {
        wrappedConnection.close();
    }

    @Override
    public void commit()
    throws SQLException {
        wrappedConnection.commit();
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements)
    throws SQLException {
        return wrappedConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob()
    throws SQLException {
        return wrappedConnection.createBlob();
    }

    @Override
    public Clob createClob()
    throws SQLException {
        return wrappedConnection.createClob();
    }

    @Override
    public NClob createNClob()
    throws SQLException {
        return wrappedConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML()
    throws SQLException {
        return wrappedConnection.createSQLXML();
    }

    @Override
    public Statement createStatement()
    throws SQLException {
        return wrappedConnection.createStatement();
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency)
    throws SQLException {
        return wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
    throws SQLException {
        return wrappedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes)
    throws SQLException {
        return wrappedConnection.createStruct(typeName, attributes);
    }

    @Override
    public boolean getAutoCommit()
    throws SQLException {
        return wrappedConnection.getAutoCommit();
    }

    @Override
    public String getCatalog()
    throws SQLException {
        return wrappedConnection.getCatalog();
    }

    @Override
    public Properties getClientInfo()
    throws SQLException {
        return wrappedConnection.getClientInfo();
    }

    @Override
    public String getClientInfo(final String name)
    throws SQLException {
        return wrappedConnection.getClientInfo(name);
    }

    @Override
    public int getHoldability()
    throws SQLException {
        return wrappedConnection.getHoldability();
    }

    @Override
    public DatabaseMetaData getMetaData()
    throws SQLException {
        return wrappedConnection.getMetaData();
    }

    @Override
    public int getTransactionIsolation()
    throws SQLException {
        return wrappedConnection.getTransactionIsolation();
    }

    @Override
    public Map<String, Class<?>> getTypeMap()
    throws SQLException {
        return wrappedConnection.getTypeMap();
    }

    @Override
    public SQLWarning getWarnings()
    throws SQLException {
        return wrappedConnection.getWarnings();
    }

    @Override
    public boolean isClosed()
    throws SQLException {
        return wrappedConnection.isClosed();
    }

    @Override
    public boolean isReadOnly()
    throws SQLException {
        return wrappedConnection.isReadOnly();
    }

    @Override
    public boolean isValid(final int timeout)
    throws SQLException {
        return wrappedConnection.isValid(timeout);
    }

    @Override
    public String nativeSQL(final String sql)
    throws SQLException {
        return wrappedConnection.nativeSQL(sql);
    }

    @Override
    public CallableStatement prepareCall(final String sql)
    throws SQLException {
        return wrappedConnection.prepareCall(sql);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
    throws SQLException {
        return wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
    throws SQLException {
        return wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql)
    throws SQLException {
        return wrappedConnection.prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys)
    throws SQLException {
        return wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes)
    throws SQLException {
        return wrappedConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames)
    throws SQLException {
        return wrappedConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
    throws SQLException {
        return wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability)
    throws SQLException {
        return wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint)
    throws SQLException {
        wrappedConnection.releaseSavepoint(savepoint);
    }

    @Override
    public void rollback()
    throws SQLException {
        wrappedConnection.rollback();
    }

    @Override
    public void rollback(final Savepoint savepoint)
    throws SQLException {
        wrappedConnection.rollback(savepoint);
    }

    @Override
    public void setAutoCommit(final boolean autoCommit)
    throws SQLException {
        wrappedConnection.setAutoCommit(autoCommit);
    }

    @Override
    public void setCatalog(final String catalog)
    throws SQLException {
        wrappedConnection.setCatalog(catalog);
    }

    @Override
    public void setClientInfo(final Properties properties)
    throws SQLClientInfoException {
        wrappedConnection.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(final String name, final String value)
    throws SQLClientInfoException {
        wrappedConnection.setClientInfo(name, value);
    }

    @Override
    public void setHoldability(final int holdability)
    throws SQLException {
        wrappedConnection.setHoldability(holdability);
    }

    @Override
    public void setReadOnly(final boolean readOnly)
    throws SQLException {
        wrappedConnection.setReadOnly(readOnly);
    }

    @Override
    public Savepoint setSavepoint()
    throws SQLException {
        return wrappedConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(final String name)
    throws SQLException {
        return wrappedConnection.setSavepoint(name);
    }

    @Override
    public void setTransactionIsolation(final int level)
    throws SQLException {
        wrappedConnection.setTransactionIsolation(level);
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> map)
    throws SQLException {
        wrappedConnection.setTypeMap(map);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface)
    throws SQLException {
        return wrappedConnection.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(final Class<T> iface)
    throws SQLException {
        return wrappedConnection.unwrap(iface);
    }

    @Override
    public int getNetworkTimeout()
    throws SQLException {
        return wrappedConnection.getNetworkTimeout();
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds)
    throws SQLException {
        wrappedConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public String getSchema()
    throws SQLException {
        return wrappedConnection.getSchema();
    }

    @Override
    public void setSchema(final String schema)
    throws SQLException {
        wrappedConnection.setSchema(schema);
    }

    @Override
    public void abort(final Executor executor)
    throws SQLException {
        wrappedConnection.abort(executor);
    }

}
