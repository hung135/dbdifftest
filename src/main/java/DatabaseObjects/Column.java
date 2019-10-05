package DatabaseObjects;

public class Column {
    public String columnName;
    public String columnType;
    public Column(String columnName,String columnType){
        this.columnName=columnName;
        this.columnType=columnType;
    }
    public String toString() {
        return this.columnName + ":" + this.columnType;
    }
} 

