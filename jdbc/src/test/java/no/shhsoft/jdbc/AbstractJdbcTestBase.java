package no.shhsoft.jdbc;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractJdbcTestBase {

    private static final Object LOCK = new Object();
    private static DataSource dataSource1;
    private static DataSource dataSource2;
    private static JdbcClient jdbcClient1;
    private static JdbcClient jdbcClient2;

    private static DataSource createHsqldbDataSource(final String dbName) {
        final PoolingDataSource ds = new PoolingDataSource();
        ds.setUserName("sa");
        ds.setPassword("");
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:mem:" + dbName);
        ds.setCheckValidityOnAllocate(true);
        ds.setValidityCheckStatement("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
        return ds;
    }

    @SuppressWarnings("unused")
    private static DataSource createPostgresqlDataSource() {
        final PoolingDataSource ds = new PoolingDataSource();
        ds.setUserName("test");
        ds.setPassword("test");
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql:test");
        ds.setCheckValidityOnAllocate(true);
        ds.setValidityCheckStatement("SELECT 1");
        return ds;
    }

    private static DataSource createDataSource(final String dbName) {
        return createHsqldbDataSource(dbName);
//        return createPostgresqlDataSource();
    }

    static DataSource getDataSource1() {
        synchronized (LOCK) {
            if (dataSource1 == null) {
                dataSource1 = createDataSource("some-database1");
            }
            return dataSource1;
        }
    }

    private static synchronized DataSource getDataSource2() {
        synchronized (LOCK) {
            if (dataSource2 == null) {
                dataSource2 = createDataSource("some-database2");
            }
            return dataSource2;
        }
    }

    static synchronized JdbcClient getJdbcClient1() {
        synchronized (LOCK) {
            if (jdbcClient1 == null) {
                jdbcClient1 = new JdbcClient(getDataSource1());
            }
            return jdbcClient1;
        }
    }

    protected static synchronized JdbcClient getJdbcClient2() {
        synchronized (LOCK) {
            if (jdbcClient2 == null) {
                jdbcClient2 = new JdbcClient(getDataSource2());
            }
            return jdbcClient2;
        }
    }

}
