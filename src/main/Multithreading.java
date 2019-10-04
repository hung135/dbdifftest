import DbConn.*;
import DataUtils.*;

public class Multithreading extends Thread {
    private DbConn connection;
    private DbConn targetConnection;
    private int numOfThreads;
    private int numberPerThread;
    private List<String> tableNames;
    private int batchSize;

    public Multithreading(DbConn connection, int threads) {
        this.connection = connection;
        this.numOfThreads = threads;
    }

    public Multithreading(DbConn connection, DbConn targetConnection, List<String> tableNames,  int batchSize, int threads){
        this.connection = conneciton;
        this.numOfThreads = threads;
        this.targetConnection = targetConnection;
        this.tableNames = tableNames;
        this.batchSize = batchSize;
    }

    public void Run(){
        try {
            DataUtils.freeWayMigrate(this.connection, 
                this.targetConnection, 
                this.tableNames, 
                this.batchSize, 
                false);
        } catch(Exception ex) {
            System.out.println(ex);
        }
    }

    public String ToString(){
        return String.format("Number of threads: %n | Number per thread: %n", this.threadNumbers, this.numberPerThread);
    }
}