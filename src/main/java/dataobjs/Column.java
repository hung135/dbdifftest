package dataobjs;

//import java.util.ArrayList;
//import java.util.List;

public class Column {
    public String columnName;
    public String columnType;
    public Column(String columnName,String columnType){
        this.columnName=columnName;
        this.columnType=columnType;
    }
    public String toString() {// overriding the toString() method
        return "\n" + this.columnName + "\n\t -> " + this.columnType;
    }
} 

