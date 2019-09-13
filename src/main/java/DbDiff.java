import net.sourceforge.jtds.jdbc.*;
//import oracle.jdbc.*;
import net.sourceforge.jtds.jdbc.Driver;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.util.Map;

public class DbDiff {



    public static void main(String[] args){
        System.out.println("Hello");

        DbConn sybaseConn = null;
        DbConn oracleConn = null;
        String sybaseDriver="net.sourceforge.jtds.jdbc.Driver";
        String oracleDriver="oracle.jdbc.OracleDriver";
        String sybaseUrl = "dbc:jtds:sybase://192.168.10.201:5000/SAMPLE";
        String oracleUrl = "jdbc:oracle:thin:@localhost:1521/orclpdb1";
        String userName="xxxx";
        String passWord="xxx";
        try {
            sybaseConn =  new DbConn();
            //sybaseConn.dbConnect(sybaseDriver, sybaseUrl, userName, passWord);
            oracleConn = new DbConn();
            //oracleConn.dbConnect(oracleDriver, oracleUrl, userName, passWord);

        }
        catch (Exception e) {
            System.out.println(e);
        }
        try {
            sybaseConn.readyaml("test.yaml");
        } catch (Exception e){

            System.out.println(e);
        }

    }
}
