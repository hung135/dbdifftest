package Interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.List;
import DatabaseObjects.Table;

public interface IDatabaseConnection {
    Connection dbGetConnPool(String driver, String jdbcUrl, String userName, String password) throws SQLException, PropertyVetoException;
    Connection getSybaseConn(String userName, String password, String host, String databasename, String port) throws SQLException;
    List<Table> getAllTableColumns(String schemaName) throws SQLException;
    List<String> getViewNames(String schemaName) throws SQLException;
    List<String> getTableNames(String schemaName)  throws SQLException;
    List<String> getSybaseObjNames(String schemaName, String objType)  throws SQLException;
    List<String> getColumns(String tabeName)  throws SQLException;
    List<String> getTriggers(String tableName)  throws SQLException;
    Boolean executeSql(String sqlText) throws SQLException;
    public Boolean query(String selectQuery) throws Exception;
    List<String[]> queryToList(String selectQuery) throws Exception;
    void queryToCSV(String selectQuery, String fullFilePath) throws Exception;
    void quertyToCSVOutputBinary(String query, String fullFilePath) throws Exception;
    void queryToExcel(String selectQuery, String sheetName, String fullFilePath) throws Exception;
    void loadCSV(String tableName, String filePath) throws IOException, SQLException;
    String getSybaseCode(String name, String objType) throws SQLException;
}