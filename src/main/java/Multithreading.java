
import java.util.List;

public class Multithreading extends Thread {
    // requried
    private DbConn connection;
    private List<DbConn> targetConnections;
    private String tableName;
    private String sql;
    private int batchSize;
    private boolean truncate;

    //outs
    private Object threadNumbers;
    private Object numberPerThread;

    public Multithreading(DbConn connection, int threads) {
        this.connection = connection;
    }

    public Multithreading(DbConn connection, List<DbConn> targetConnections, String tableName, String sql, int batchSize,
            boolean truncate) {
        this.connection = connection;
        this.targetConnections = targetConnections;
        this.tableName = tableName;
        this.sql = sql;
        this.batchSize = batchSize;
        this.truncate = truncate;
    }

    public void Run() {
        try {
            DataUtils.freeWayMigrateMulti(this.connection, this.targetConnections, this.sql, this.tableName,
                    this.batchSize, this.truncate);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public String ToString() {
        return String.format("Number of threads: %n | Number per thread: %n", this.threadNumbers, this.numberPerThread);
    }
}