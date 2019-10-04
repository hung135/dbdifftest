import DbConn.*;
import DataUtils.*;

public class Multithreading extends Thread {
    private DbConn connection;
    private List<DbConn> targetConnections;
    private String tableName;
    private String sql;
    private int batchSize;
    private boolean truncate;

    public Multithreading(DbConn connection, int threads) {
        this.connection = connection;
        this.numOfThreads = threads;
    }

    public Multithreading(DbConn connection, List<DbConn> targetConnection, String tableName, String sql, int batchSize, boolean truncate){
        this.connection = conneciton;
        this.targetConnection = targetConnection;
        this.tableName = tableName;
        this.sql = sql;
        this.batchSize = batchSize;
        this.truncate = truncate;
    }

    public void Run(){
        try {
            DataUtils.freeWayMigrateMulti(
                this.connection,
                this.targetConnection,
                this.sql,
                this.tableName,
                this.batchSize,
                this.truncate
            );
        } catch(Exception ex) {
            System.out.println(ex);
        }
    }

    public String ToString(){
        return String.format("Number of threads: %n | Number per thread: %n", this.threadNumbers, this.numberPerThread);
    }
}