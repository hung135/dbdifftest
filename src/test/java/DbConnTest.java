
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.opencsv.CSVWriter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbConnTest {
    public Map<String, String> env = System.getenv();

    @Test
    @Disabled
    public void testReadFileExecuteSQL() throws Exception {

        String password = env.get("SYBASE_PASSWORD");

        DbConn sybaseDBConn = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        File file = new File(getClass().getClassLoader().getResource("create_sybase.sql").getFile());

        // System.out.println(file.toString());

        BufferedReader br = new BufferedReader(new FileReader(file));
        String sqlText = "";
        String st;
        while ((st = br.readLine()) != null)
            sqlText = sqlText + " " + st;
        br.close();
        // System.out.println(sqlText);
        sybaseDBConn.executeSql(sqlText);

    }

    @Test
    public void testQueryToCSV() throws Exception {
        String selectQuery = "select * from dbo.titles";
        String csvFilePath = "/workspace/test.csv";
        String password = env.get("SYBASE_PASSWORD");
        DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        try {
            db.queryToCSV(selectQuery, csvFilePath);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Test
    @Disabled
    public void testQueryToStoredProc() throws Exception {
        String selectQuery = "SELECT u.name as name1, o.name, c.text FROM sysusers u, syscomments c, sysobjects o "
                + "WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid ORDER BY o.id,    c.colid";
        String csvFilePath = "/workspace/test.csv";
        String password = env.get("SYBASE_PASSWORD");
        DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        try {
            db.queryToCSV(selectQuery, csvFilePath);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * this test is hear cause the code is very similar to code above
     *
     * @throws SQLException
     * @throws IOException
     */
    @Test
    public void testWriteListStringToCSV() throws Exception {
        String selectQuery = "SELECT u.name as name1, o.name, c.text FROM sysusers u, syscomments c, sysobjects o "
                + "WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid ORDER BY o.id,  c.colid";
        String csvFilePath = "/workspace/testxxxx.csv";
        String password = env.get("SYBASE_PASSWORD");
        DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        List<String[]> rows = new ArrayList<>();
        try {
            if (db.query(selectQuery) == true) {

                /**
                 *
                 * processing through the result set
                 */

                ResultSetMetaData metadata = db.rs.getMetaData();
                int columnCount = metadata.getColumnCount();
                String stringRow[] = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    /** Adding header row */
                    stringRow[i - 1] = (metadata.getColumnName(i));
                }
                rows.add(stringRow);
                System.out.println(rows);
                while (db.rs.next()) {
                    stringRow = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        String tmp = db.rs.getString(i);
                        if (i == 2) {
                            /**
                             * test some special logic
                             */

                            tmp = String.join(", ", DataUtils.findTablesFromInsert(tmp));
                            tmp = "xxxtest";
                        }
                        stringRow[i - 1] = "sssss";
                    }
                    /** Adding data row */
                    rows.add(stringRow);
                }
                CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
                Boolean includeHeaders = true;

                writer.writeAll(rows, includeHeaders);
                writer.close();
                db.rs.close();
                db.conn.close();
            }
            ;

        } catch (Exception e) {
            System.out.println(e);
            db.rs.close();
            db.conn.close();
        }
    }

    @Test
    @Disabled
    public void testCallStmnt() throws Exception {
        String password = env.get("SYBASE_PASSWORD");
        DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        Statement stmt = null;
        String sql = "select distinct obj.name, c.text from dbo.sysobjects obj join dbo.syscolumns col on col.id = obj.id join dbo.syscomments c on obj.id=c.id"
                + " join dbo.systypes tp on col.usertype = tp.usertype  where obj.type = 'V'  and obj.name='testview' order by 1";

        stmt = db.conn.createStatement();

        // Let us check if it returns a true Result Set or not.
        ResultSet rs = stmt.executeQuery(sql);
        String lastTableName = "";
        String currTableName = "";
        String currDDL = "";
        Map<String, String> viewDDLHashMap = new HashMap<String, String>();
        while (rs.next()) {
            currTableName = rs.getString(1);
            String snippetDDL = rs.getString(2);
            if (!lastTableName.contentEquals(currTableName)) {
                if (!lastTableName.equals("")) {
                    viewDDLHashMap.put(lastTableName, currDDL);
                }
                lastTableName = currTableName;
                System.out.println(currDDL);

                currDDL = snippetDDL;

            } else {
                currDDL = currDDL + snippetDDL;
            }

        }

        if (!currTableName.equals("")) {
            viewDDLHashMap.put(lastTableName, currDDL);
        }
        stmt.close();
        db.conn.close();
        System.out.println(viewDDLHashMap.size());
    }

    @Test
    @Disabled
    public void testGetViewDDL() throws Exception {
        String password = env.get("SYBASE_PASSWORD");
        DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        List<String> views = db.getViewNames("dbo");
        for (String view : views) {
            System.out.println(db.getSybaseViewDDL(view));
        }
    }

    @Test
    public void testProcNames() throws Exception {
        String password = env.get("SYBASE_PASSWORD");
        DbConn db = new DbConn(DbConn.DbType.SYBASE, "sa", password, "dbsybase", "5000", "master");
        List<String> items = db.getProcNames("dbo");

        for (String item : items) {
            System.out.println(item);
            System.out.println(db.getSybaseProcDDL(item));
        }
    }
}
