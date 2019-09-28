import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hpsf.Array;

public class DbThreader extends Object implements Runnable {
    private DbConn srcDbConn;
    private List<DbConn> trgConns;
    private List<String> tableNames;
    private int batchSize;
    private Boolean truncate;

    /**clone the connections  */
    public DbThreader(DbConn srcConn, List<DbConn> trgConn, List<String> tableNames, int batchSize, Boolean truncate)
            throws CloneNotSupportedException, ClassNotFoundException, SQLException {
        this.srcDbConn = srcConn.clone();
        this.srcDbConn.reConnect();
        this.trgConns = new ArrayList<>();
        this.batchSize = batchSize;
        this.truncate = truncate;
        for (DbConn conn : trgConn) {
            DbConn tmpConn = conn.clone();
            tmpConn.reConnect();
            this.trgConns.add(tmpConn);

        }

    }

    public void run() {
        try {
            DataUtils.freeWayMigrate(srcDbConn, trgConns, tableNames, batchSize, truncate);
        } catch (SQLException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }      
}
