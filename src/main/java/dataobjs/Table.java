package dataobjs;

import java.util.ArrayList;
import java.util.List;

public class Table {
    public String tableName;
    public List<String> columnNames;

    public Table(String tableName) {
        this.tableName = tableName;
        this.columnNames = new ArrayList<>();
    }

    public String toString() {// overriding the toString() method
        return "\n" + this.tableName + "\n\t -> " + this.columnNames;
    }

}