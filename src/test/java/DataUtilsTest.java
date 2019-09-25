
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataUtilsTest {
    public Map<String, String> env = System.getenv();

    // @Test
    // public void testFunctions() {

    // System.out
    // .println(DataUtils.RegexExtract("\\w*.\\.\\.", " from db1..table1,
    // db2..table2, db3..table3 'b' 'c'"));
    // System.out.println(DataUtils.FindSybaseDatabase(
    // " from db1..table1, db2..table2, db3..table3, db3.dbo.table4 where something
    // = something '"));
    // System.out.println(DataUtils.findTablesFromQuery(
    // " dsfasdfa tdhei screasdf; asdfasdf before select select col1, (select 1 from
    // xxx.xx) as z, aa,(select 1 from xxx.xx) from schema.table join x.table2 where
    // x=(select abc from test.table1) as y "));
    // System.out.println(DataUtils.findTablesFromInsert(
    // " insert into xx.table1 (col1,col2) values (1,2),(2,3); insert into yy.table2
    // as select * from test.table1"));
    // }

    // @Test
    // public void testPostgres() throws SQLException {
    // Map<String, String> env = System.getenv();
    // String url = "jdbc:postgresql://dbpg/postgres";
    // Properties props = new Properties();
    // props.setProperty("user", "root");
    // props.setProperty("password", env.get("PGPASSWORD"));

    // System.out.println(props);

    // Connection conn = DriverManager.getConnection(url, props);

    // }

    @Test
    public void testOracleDbConn() throws SQLException, PropertyVetoException {
    DbConn db = new DbConn(DbConn.DbType.ORACLE, "system", "Docker12345", "dboracle", "1521", "dockerdev");
    List<String> x = db.getTableNames("dbo");
    System.out.println("Connect to Oracle Successful" + x);

    }

    // @Test
    // public void testOracle() throws SQLException {
    // Map<String, String> env = System.getenv();
    // Connection conn = null;
    // String url = "jdbc:oracle:thin:@dboracle:1521:dev";
    // Properties props = new Properties();
    // props.setProperty("user", "system");
    // props.setProperty("password", env.get("ORACLE_PWD"));
    // // props.setProperty("ssl", "disable");
    // System.out.println(props);
    // conn = DriverManager.getConnection(url, props);
    // System.out.println("Connect to Oracle Successful");

    // }

    // @Test
    // public void testSybase() throws SQLException {
    // Map<String, String> env = System.getenv();
    // Connection conn = null;

    // String url = "jdbc:jtds:sybase://dbsybase:5000/";
    // Properties props = new Properties();
    // props.setProperty("user", "sa");
    // props.setProperty("password", env.get("SYBASE_PASSWORD"));

    // // System.out.println(props);
    // conn = DriverManager.getConnection(url, props);
    // System.out.println("Connect to Sysbase Successful");

    // }

    // @Test
    // public void testSybaseObj() throws SQLException {

    // String password = env.get("SYBASE_PASSWORD");
    // DbConn sybaseDBConn = new DbConn(DbConn.DbType.SYBASE, "sa", password,
    // "dbsybase", "5000", "master");
    // // Connection conn = db.getSybaseConn("sa", password, "dbsybase", "master",
    // // "5000");

    // }

    // @Test
    // public void testSybaseConnPooling() throws SQLException {
    // Map<String, String> env = System.getenv();
    // DbConn db = new DbConn();

    // String url = "jdbc:jtds:sybase://dbsybase:5000/";

    // String password = env.get("SYBASE_PASSWORD");

    // Connection conn = db.dbGetConnPool("net.sourceforge.jtds.jdbc.Driver", url,
    // "sa", password);
    // System.out.println("Connect to Sysbase Successful");

    // }

    // @Test
    // public void testSybaseGetProcs() throws SQLException {
    // Map<String, String> env = System.getenv();
    // String password = env.get("SYBASE_PASSWORD");
    // DbConn db = new DbConn();

    // Connection conn = db.getSybaseConn("sa", password, "dbsybase", "master",
    // "5000");

    // DataUtils.getSybaseStoredProcs(conn);
    // }

    // @Test
    // public void testSybaseQuery() throws SQLException {
    // Map<String, String> env = System.getenv();
    // Connection conn = null;

    // String url = "jdbc:jtds:sybase://dbsybase:5000/";
    // Properties props = new Properties();
    // props.setProperty("user", "sa");
    // props.setProperty("password", env.get("SYBASE_PASSWORD"));

    // System.out.println(props);
    // conn = DriverManager.getConnection(url, props);

    // String query = "SELECT u.name as name1, o.name, c.text FROM sysusers u,
    // syscomments c, sysobjects o "
    // + "WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid ORDER BY o.id,
    // c.colid";

    // Statement stmt = conn.createStatement();

    // ResultSet rs = stmt.executeQuery(query);

    // while (rs.next()) {
    // String name1 = rs.getString("name1");
    // String name = rs.getString("name");
    // String txt = rs.getString("text");

    // System.out.println(name1 + ", " + name + ", " + txt);
    // }

    // }

    // @Test
    // public void testReadFileExecuteSQL() throws SQLException, IOException {

    // String password = env.get("SYBASE_PASSWORD");

    // DbConn sybaseDBConn = new DbConn(DbConn.DbType.SYBASE, "sa", password,
    // "dbsybase", "5000", "master");
    // File file = new
    // File(getClass().getClassLoader().getResource("create_sybase.sql").getFile());

    // // System.out.println(file.toString());

    // BufferedReader br = new BufferedReader(new FileReader(file));
    // String sqlText = "";
    // String st;
    // while ((st = br.readLine()) != null)
    // sqlText = sqlText + " " + st;
    // br.close();
    // // System.out.println(sqlText);
    // sybaseDBConn.executeSql(sqlText);

    // }

    // @Test
    // public void testGetTableNames() throws SQLException, IOException {

    // String password = env.get("SYBASE_PASSWORD");
    // DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase",
    // "5000", "master");
    // // db.getTableNames("dbo");
    // // db.getColumn("titles1");
    // System.out.println(db.getTableColumns("dbo"));
    // }

    @Test
    @Disabled
    public void SkipMe() {
        assertTrue(false);
    }

    public void RunMe() {
        assertTrue(true);
    }

    @Test
    @Disabled
    public void testCompareCSV() throws Exception {
        String firstCSV = "/workspace/jython_scripts/table_rowcount.csv";
        String secondCSV = "/workspace/jython_scripts/table_rowcount2.csv";
        String outFile = "/workspace/jython_scripts/reports/output.csv";
        List<String> primaryColumn = new ArrayList<>();
        primaryColumn.add("TableName");

        DataUtils.compareCSV(firstCSV, secondCSV, outFile, primaryColumn, "TableName,Dev,Test", "data");
    }
}
