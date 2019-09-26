
import com.google.common.io.ByteSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.stream.Stream;
import java.sql.*;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataobjs.Table;

import java.io.FileOutputStream;
import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import java.util.*;

public class DbConn {
    public Connection conn;
    // final static Logger logger = Logger.getLogger(DbConn.class);
    private Statement stmt; // tbd
    public ResultSet rs;
    public DbType dbType;
    public String databaseName;
    public String url;

    public enum DbType {
        // jdbc:oracle:thin:scott/tiger@//myhost:1521/myservicename
        ORACLE("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@{0}:{1}:{2}"),
        ORACLESID("oracle.jdbc.OracleDriver",
                "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST={0})(PORT={1})))(CONNECT_DATA=(SERVICE_NAME={2})))"),
        SYBASE("net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sybase://{0}:{1}/{2}"),
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

    public String getUrl() {
        return this.url;
    }

    public DbConn(DbType dbtype, String userName, String password, String host, String port, String databaseName)
            throws SQLException, PropertyVetoException, ClassNotFoundException {

        this.dbType = dbtype;
        this.databaseName = databaseName;

        String url = MessageFormat.format(dbtype.url, host, port, databaseName);
        this.url = url;
        Properties props = new Properties();
        props.setProperty("user", userName);
        props.setProperty("password", password);
        Class.forName(dbtype.driver);
        this.conn = DriverManager.getConnection(url, props);
        System.out.println("Connect to Oracle Successful");
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
    public List<Table> getAllTableColumns(String schemaName) throws SQLException {
        List<String> tables = this.getTableNames(schemaName);
        List<Table> items = new ArrayList<>();

        // For Loop for iterating ArrayList
        for (int i = 0; i < tables.size(); i++) {
            String tableName = tables.get(i);
            Table tbl = new Table(tableName);
            tbl.columnNames = this.getColumns(tableName);
            items.add(tbl);
        }
        return items;
    }

    public List<String> getViewNames(String schemaName) throws SQLException {

        String TABLE_NAME = "TABLE_NAME";
        String TABLE_SCHEMA = "TABLE_SCHEM";
        String[] VIEW_TYPES = { "VIEW" };
        DatabaseMetaData dbmd = conn.getMetaData();

        ResultSet rs = dbmd.getTables(this.databaseName, schemaName, null, VIEW_TYPES);
        List<String> items = new ArrayList<>();
        while (rs.next()) {

            if (schemaName.toLowerCase().equals(rs.getString(TABLE_SCHEMA).toLowerCase())) {
                items.add(rs.getString(TABLE_NAME));
            }
        }
        return items;
    }

    public List<String> getTableNames(String schemaName) throws SQLException {
        String TABLE_NAME = "TABLE_NAME";
        String TABLE_SCHEMA = "TABLE_SCHEM";
        String[] TYPES = { "TABLE" };
        List<String> items = new ArrayList<>();
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        // Print TABLE_TYPE "TABLE"
        ResultSet rs = databaseMetaData.getTables(this.databaseName, schemaName, null, TYPES);

        while (rs.next()) {

            if (schemaName.toLowerCase().equals(rs.getString(TABLE_SCHEMA).toLowerCase())) {
                items.add(rs.getString(TABLE_NAME));
            }
        }
        return items;
    }

    public List<String> getSybaseObjNames(String schemaName, String objType) throws SQLException {
        String sql = "select distinct obj.name from dbo.sysobjects obj  join dbo.syscomments c on obj.id=c.id"
                + "   where obj.type = '" + objType + "'  and USER_NAME(uid)='" + schemaName + "' order by 1";

        List<String> items = new ArrayList<>();
        // Print TABLE_TYPE "TABLE"

        Statement stmt = null;
        stmt = this.conn.createStatement();
        // Let us check if it returns a true Result Set or not.
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            items.add(rs.getString(1));

        }
        stmt.close();

        rs.close();

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
    public List<String> getColumns(String tabeName) throws SQLException {
        List<String> items = new ArrayList<>();
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet resultSet = databaseMetaData.getColumns(this.databaseName, null, tabeName, null);
        while (resultSet.next()) {
            // Print
            // System.out.println(resultSet.getString("COLUMN_NAME"));
            items.add(resultSet.getString("COLUMN_NAME"));
        }
        return items;
    }

    public List<String> getTriggers(String tableName) throws SQLException {
        List<String> items = new ArrayList<>();
        DatabaseMetaData databaseMetaData = conn.getMetaData();

        ResultSet result = databaseMetaData.getTables(this.databaseName, null, tableName, new String[] { "TRIGGER" });
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
        /*
         * for (int i = 1; i <= columnCount; i++) {
         * System.out.println(metadata.getTableName(i) + " senstive: " +
         * metadata.isCaseSensitive(i) + " name: " + metadata.getCatalogName(i) +
         * " type: " + metadata.getColumnTypeName(i) + " schema: " +
         * metadata.getSchemaName(i) + " col: " + metadata.getColumnName(i)); }
         */
        while (this.rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                /** Adding header row */
                // byte[] data = rs.getBytes(i);
                // row[i - 1] = new String(data, StandardCharsets.UTF_8);
                row[i - 1] = (this.rs.getString(i));
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

    public void quertyToCSVOutputBinary(String query, String fullFilePath) throws Exception {
        Statement stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData metadata = rs.getMetaData();

        int columnCount = metadata.getColumnCount();
        List<Integer> imageColIndex = new ArrayList<Integer>();
        List<Integer> stringColIndex = new ArrayList<Integer>();
        String[] allColumnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            String type = metadata.getColumnTypeName(i);
            String columnName = metadata.getColumnName(i);
            System.out.println(columnName + " -- " + type);
            // For now; later create custom enum. "image" isn't supported by JAVA
            List<String> dataTypes=Arrays.asList("VARBINARY","BINARY","CLOB","BLOB","image");
            
            if (dataTypes.contains(type.toLowerCase())) {
                imageColIndex.add(i);
            } else {
                stringColIndex.add(i);
            }
            allColumnNames[i - 1] = columnName;
        }

        List<String[]> data = new ArrayList<String[]>();
        allColumnNames = Arrays.copyOf(allColumnNames, allColumnNames.length + imageColIndex.size());
        // get values up here
        // Map<Integer, Map<Integer, String>> output = new HashMap<Integer, Map<Integer,
        // String>>();
        // String tblName = metadata.getTableName(1);
        // for (Integer i : columnTypeIndex) {
        // columnNames[i] = metadata.getColumnName(i) + "_output";
        // output.put(columnCount + i, DataUtils.outputBinary(this.conn, fullFilePath,
        // tblName,
        // metadata.getColumnName(i), metadata.getColumnTypeName(i)));
        // }
         
        // declare it once no for each row
        int ii=0;
        while (rs.next()) {
            ii++;
            String[] row = new String[columnCount];
             
            for (int stringIdx : stringColIndex) {
                row[stringIdx - 1] = rs.getString(stringIdx);
            }
            for (int imgIdx : imageColIndex) {
                
//                InputStream in =  ByteSource.wrap().openStream();
            
  //              int length = in.available();
                //get bytes faster than getinputstream but need enough memory to hold entire blob
                byte[] blobBytes = rs.getBytes(imgIdx);
                //in.read(blobBytes);

                String md5Hex = DigestUtils.md5Hex(blobBytes).toUpperCase();

                // change name here
                String dirPath = fullFilePath + "/image/" + md5Hex;
                File blobFile = new File(dirPath);
                if (!blobFile.getParentFile().exists()) {
                    blobFile.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(blobFile);

                fos.write(blobBytes);
                fos.close();
                //System.out.println("Image: "+ii+" "+length+" "+md5Hex);
                row[imgIdx - 1] = dirPath;
                if (Math.floorMod(ii, 1000)==0){
                    System.out.println("Records Dumped: "+ii);
                }
            }
            data.add(row);

        }

        CSVWriter writer = new CSVWriter(new FileWriter(fullFilePath + "/index.csv"));
        Boolean includeHeaders = true;
        // this is the header
        data.add(0, allColumnNames);
        writer.writeAll(data, includeHeaders);

        writer.close();

    }

    public void print_test(String selectQuery) {
        System.out.println(selectQuery);
    }

    public void queryToExcel(String selectQuery, String sheetName, String fullFilePath) throws Exception {

        Statement stmt = this.conn.createStatement();

        /* Define the SQL query */
        ResultSet query_set = stmt.executeQuery(selectQuery);
        /* Create Map for Excel Data */
        Map<String, Object[]> excel_data = new HashMap<String, Object[]>(); // create a map and define data
        int row_counter = 0;

        ResultSetMetaData metadata = query_set.getMetaData();
        int columnCount = metadata.getColumnCount();
        List<String> columnNames = new ArrayList<>();

        for (int i = 1; i <= columnCount; i++) {

            columnNames.add(metadata.getColumnName(i));

            // System.out.println(" " + metadata.getColumnName(i));

        }

        /* Populate data into the Map */
        while (query_set.next()) {
            row_counter = row_counter + 1;

            String[] row_data = new String[columnCount];

            // Data rows
            for (int i = 1; i <= columnCount; i++) {
                row_data[i - 1] = query_set.getString(columnNames.get(i - 1));
            }

            excel_data.put(Integer.toString(row_counter), row_data);
        }
        /* Close all DB related objects */
        query_set.close();
        stmt.close();
        DataUtils.writeToExcel(columnNames, excel_data, sheetName, fullFilePath);
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
        tableColumns = this.getColumns(tableName);
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

    public String getSybaseViewDDL(String schemaName, String viewName) throws SQLException {

        Statement stmt = null;
        String sql = "select distinct obj.name, c.text from dbo.sysobjects obj  join dbo.syscomments c on obj.id=c.id"
                + "   where obj.type = 'V'  and USER_NAME(uid)='" + schemaName + "' and obj.name='" + viewName
                + "' order by 1";
        stmt = this.conn.createStatement();
        // Let us check if it returns a true Result Set or not.
        ResultSet rs = stmt.executeQuery(sql);
        String currDDL = null;
        while (rs.next()) {
            String snippetDDL = rs.getString(2);
            currDDL = (snippetDDL == null) ? currDDL + " " : currDDL + snippetDDL;
        }
        stmt.close();

        return currDDL;
    }

    public String getSybaseCode(String name, String objType) throws SQLException {

        Statement stmt = null;
        String sql = "select distinct obj.type, obj.name, c.text from dbo.sysobjects obj join dbo.syscomments c on obj.id=c.id"
                + "  where obj.type in ('" + objType + "')  and obj.name='" + name + "' order by 1";
        stmt = this.conn.createStatement();
        // Let us check if it returns a true Result Set or not.

        ResultSet rs = stmt.executeQuery(sql);
        String currDDL = null;
        while (rs.next()) {
            String snippetDDL = rs.getString(3);
            currDDL = (snippetDDL == null) ? currDDL + " " : currDDL + snippetDDL;
        }
        stmt.close();

        return currDDL;
    }

}
