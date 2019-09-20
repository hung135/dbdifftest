
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.Charsets;

//import org.apache.log4j.Logger;

import oracle.net.aso.i;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.sql.*;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataobjs.*;

public class DbConn {
    private Connection conn;
    // final static Logger logger = Logger.getLogger(DbConn.class);
    private Statement stmt; // tbd
    public ResultSet rs;
    public DbType dbType;

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
            // System.out.println(url);
        }

        public String driver() {
            // System.out.println(driver);
            return driver;
        }

        public String url() {
            System.out.println(url);
            return url;
        }

        public static DbType getMyEnumIfExists(String value) {
            for (DbType db : DbType.values()) {
                if (db.name().equalsIgnoreCase(value))
                    return db;
            }
            return null;
        }
    }

    public DbConn(DbType dbtype, String userName, String password, String host, String port, String databaseName)
            throws SQLException, PropertyVetoException {
        this.dbType = dbtype;
        ComboPooledDataSource cpds = new ComboPooledDataSource();

        cpds.setDriverClass(dbtype.driver());

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
        // System.out.println("DB Connection Successful: " + dbtype);
    }

    public Connection dbGetConnPool(String driver, String jdbcUrl, String userName, String password)
            throws SQLException, PropertyVetoException {

        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass(driver);

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

    /**
     * 
     * ToDos: Don't for get to figure out how to close the stmnt and rs , Executes a
     * quey and sets the instance rs variable to be used later.
     * 
     * @param selectQuery
     * @return Boolean stating if there are records
     * @throws Exception
     */
    public Boolean query(String selectQuery) throws Exception {
        Boolean hasRecords = false;

        Statement stmt = this.conn.createStatement();
        this.rs = stmt.executeQuery(selectQuery);
        if (this.rs.next() == true) {
            hasRecords = true;
            this.rs.beforeFirst();
        }

        return hasRecords;

    }

    public List<String[]> queryToList(String selectQuery) throws Exception {
        List<String[]> items = new ArrayList<>();

        Statement stmt = this.conn.createStatement();
        this.rs = stmt.executeQuery(selectQuery);
        ResultSetMetaData metadata = this.rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        while (this.rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                /** Adding header row */
                byte[] data = rs.getBytes(i);
                row[i - 1] = new String(data, StandardCharsets.UTF_8);
                //row[i - 1] = (this.rs.getString(i));
            }
            items.add(row);
        }

        return items;

    }

    /**
     * Take a query and writes it to a CSV file with the header
     * 
     * @param selectQuery
     * @param fullFilePath
     * @throws Exception
     */
    public void queryToCSV(String selectQuery, String fullFilePath) throws Exception {
        // System.out.println(selectQuery);

        // System.out.println("Writing to file: " + fullFilePath);
        Statement stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectQuery);
        // int numCols = rs.getMetaData().getColumnCount();
        // System.out.println(selectQuery);

        CSVWriter writer = new CSVWriter(new FileWriter(fullFilePath));
        Boolean includeHeaders = true;

        writer.writeAll(rs, includeHeaders);

        writer.close();

    }

    public void print_test(String selectQuery) {
        System.out.println(selectQuery);
    }

    /**
     * still in progress
     * 
     * @param tableName
     * @param filePath
     * @throws IOException
     * @throws SQLException
     */
    public void loadCSV(String tableName, String filePath) throws IOException, SQLException {
        List<String> tableColumns;

        PreparedStatement preparedStatement = null;

        List<String> question = new ArrayList<>();
        tableColumns = this.getColumn(tableName);
        for (int i = 0; i < tableColumns.size(); i++) {
            question.add("?");
        }
        String columns = String.join(",", question);
        String sql = "Insert into " + tableName + " values(" + columns + ")";

        preparedStatement = this.conn.prepareStatement(sql);

        // CSVFormat fmt =
        // CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withRecordSeparator("\r\n");

        Reader file = new FileReader("path/to/file.csv");

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(file);
        for (CSVRecord record : records) {
            for (int i = 0; i < tableColumns.size(); i++) {
                preparedStatement.setString(i, record.get(tableColumns.get(i)));
            }
            preparedStatement.addBatch();
        }

        int[] affectedRecords = preparedStatement.executeBatch();
        System.out.println("Total rows Inserted: " + affectedRecords);
        preparedStatement.close();

    }

}
