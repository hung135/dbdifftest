import com.mchange.v2.c3p0.ComboPooledDataSource;

import oracle.net.aso.i;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.sql.*;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import dataobjs.*;

public class DbConn {
    private Connection conn;

    public enum DbType {
        ORACLE("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@{0}:{1}:{2}"),
        SYBASE("net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sybase://{0}:{1}:{2}"),
        POSTGRES("org.postgresql.Driver", "jdbc:postgresql://{0}:{1}:{2}"),
        MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://{0}:{1}/{2}");

        private String driver;
        private String url;

        DbType(String driver, String url) {
            this.driver = driver;
            this.url = url;
        }

        public String driver() {
            return driver;
        }

        public String url() {
            return url;
        }
    }

    DbConn(DbType dbtype, String userName, String password, String host, String port, String databaseName)
            throws SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(dbtype.driver());
        } catch (PropertyVetoException e) {
        }
        String url = MessageFormat.format(dbtype.url, host, port, databaseName);

        cpds.setJdbcUrl(url);
        cpds.setUser(userName);
        cpds.setPassword(password);
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);
        cpds.setMaxIdleTime(60);
        cpds.setMaxStatements(100);
        cpds.setPreferredTestQuery("SELECT 1");
        cpds.setIdleConnectionTestPeriod(60);
        cpds.setTestConnectionOnCheckout(true);

        this.conn = cpds.getConnection();

    }

    public Connection dbGetConnPool(String driver, String jdbcUrl, String userName, String password)
            throws SQLException {

        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(driver);
        } catch (PropertyVetoException e) {
        }
        cpds.setJdbcUrl(jdbcUrl);
        cpds.setUser(userName);
        cpds.setPassword(password);
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);
        cpds.setMaxIdleTime(60);
        cpds.setMaxStatements(100);
        cpds.setPreferredTestQuery("SELECT 1");
        cpds.setIdleConnectionTestPeriod(60);
        cpds.setTestConnectionOnCheckout(true);

        Connection conn = cpds.getConnection();
        return conn;
    }

    public Connection getSybaseConn(String userName, String password, String host, String databasename, String port)
            throws SQLException {
        Connection conn = null;

        String url = "jdbc:jtds:sybase://{0}:{1}/{2}";

        url = MessageFormat.format(url, host, port, databasename);

        Properties props = new Properties();
        props.setProperty("user", userName);
        props.setProperty("password", password);
        props.setProperty("host", host);
        props.setProperty("port", port);
        conn = DriverManager.getConnection(url, props);
        return conn;
    }

    /**
     * 
     * 
     * This could be faster with a query that returns it all in 1 pass
     * 
     * @param schemaName
     * @return
     * @throws SQLException
     */
    public List<Table> getTableColumns(String schemaName) throws SQLException {
        List<String> tables = this.getTableNames(schemaName);
        List<Table> items = new ArrayList<>();

        // For Loop for iterating ArrayList
        for (int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i);
            Table tbl = new Table(tableName);
            tbl.columnNames = this.getColumn(tableName);
            items.add(tbl);

        }
        return items;
    }

    public List<String> getTableNames(String schemaName) throws SQLException {
        List<String> items = new ArrayList<>();
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        // Print TABLE_TYPE "TABLE"
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] { "TABLE" });

        while (resultSet.next()) {
            // Print
            // System.out.println(resultSet.getString("TABLE_NAME"));
            items.add(resultSet.getString("TABLE_NAME"));
        }
        return items;
    }

    /**
     * 
     * 
     * 
     * https://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html
     * 
     * @param conn
     * @param tabeName
     * @return
     * @throws SQLException
     */
    public List<String> getColumn(String tabeName) throws SQLException {
        List<String> items = new ArrayList<>();
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet resultSet = databaseMetaData.getColumns(null, null, tabeName, null);
        while (resultSet.next()) {
            // Print
            // System.out.println(resultSet.getString("COLUMN_NAME"));
            items.add(resultSet.getString("COLUMN_NAME"));
        }
        return items;
    }

    public List<String> getTriggers(String tabeName) throws SQLException {
        List<String> items = new ArrayList<>();
        DatabaseMetaData databaseMetaData = conn.getMetaData();

        ResultSet result = databaseMetaData.getTables("%", null, "%", new String[] { "TRIGGER" });
        while (result.next()) {
            items.add(result.getString("TABLE_NAME"));
        }
        return items;
    }

    public Boolean executeSql(String sqlText) throws SQLException {

        Statement stmt = null;
        stmt = conn.createStatement();

        // Let us check if it returns a true Result Set or not.
        Boolean ret = stmt.execute(sqlText);
        stmt.close();
        return ret;

    }

}
