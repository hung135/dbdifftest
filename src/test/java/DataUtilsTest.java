
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;
import java.util.Map;

public class DataUtilsTest extends TestCase {
    // @Ignore
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

    // @Ignore
    // public void testDbFunctions() {
    // DbConn sybaseConn = null;

    // String sybaseDriver = "net.sourceforge.jtds.jdbc.Driver";

    // String sybaseUrl = "dbc:jtds:sybase://192.168.1.88:8000/MYSYBASE";
    // String postgresDriver = "org.postgresql.Driver";
    // String userName = "sa";
    // String passWord = "myPassword";
    // sybaseConn = new DbConn();
    // try {
    // sybaseConn.dbConnect(sybaseDriver, sybaseUrl, userName, passWord);
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    // }

    // public void testPostgres() {
    // Map<String, String> env = System.getenv();
    // String url = "jdbc:postgresql://dbpg/postgres";
    // Properties props = new Properties();
    // props.setProperty("user", "root");
    // props.setProperty("password", env.get("PGPASSWORD"));
    // // props.setProperty("ssl", "disable");
    // System.out.println(props);
    // try {
    // Connection conn = DriverManager.getConnection(url, props);
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    // }

    public void testOracle() {
        Map<String, String> env = System.getenv();
        String url = "jdbc:oracle:thin:@dboracle:1521:dev";
        Properties props = new Properties();
        props.setProperty("user", "system");
        props.setProperty("password", env.get("ORACLE_PWD"));
        // props.setProperty("ssl", "disable");
        System.out.println(props);
        try {
            Connection conn = DriverManager.getConnection(url, props);
            // Connection conn =
            // DriverManager.getConnection("jdbc:oracle:thin:@dboracle:1521:orcl", "system",
            // "Docker12345");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // public void testSybase() {
    // Map<String, String> env = System.getenv();

    // // SYBASE_USER: sa
    // // SYBASE_PASSWORD: password
    // String url = "jdbc:jtds:sybase://dbsybase:5000/";
    // Properties props = new Properties();
    // props.setProperty("user", "sa");
    // props.setProperty("password", env.get("SYBASE_PASSWORD"));
    // // props.setProperty("ssl", "disable");
    // System.out.println(props);
    // try {
    // Connection conn = DriverManager.getConnection(url, props);
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    // }
}
