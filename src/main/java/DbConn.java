import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.yaml.snakeyaml.Yaml;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DbConn {

    DbConn()
    {
        System.out.println("Constructor called");
    }
    public Connection dbConnect(String driver, String jdbcUrl, String userName, String password) throws SQLException {

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
        cpds.setTestConnectionOnCheckout( true );


        Connection conn = cpds.getConnection();
        return conn;
    }












    public  void readyaml(String yamlFilePath){



        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(yamlFilePath);
        Map<String, Object> obj = yaml.load(inputStream);
        System.out.print(obj);

    }
}
