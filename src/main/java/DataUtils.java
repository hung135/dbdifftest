import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.yaml.snakeyaml.Yaml;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Statement;

import java.io.FileWriter;
import java.io.IOException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.FileReader;
import java.sql.Blob;

import java.security.KeyPair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.Map;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

/**
 * Functions we need to write that process some data, put them here
 */
public class DataUtils {
    // final static Logger logger = Logger.getLogger(DataUtils.class);

    // Generic function to convert list to set
    public static <T> Set<T> convertListToSet(List<T> list) {
        // create an empty set
        Set<T> set = new HashSet<>();

        // Add each element of list into the set
        for (T t : list)
            set.add(t);

        // return the set
        return set;
    }

    /**
     * Take a regex and a data string and extract all the data that matches the
     * regex and returns it as a collection of string
     * 
     * @param regExString
     * @param dataString
     */
    public static Set<String> RegexExtract(String regExString, String dataString) {
        Set<String> xx = new HashSet<String>();

        Pattern pattern = Pattern.compile(regExString);
        Matcher matcher = pattern.matcher(dataString);
        while (matcher.find()) {
            // System.out.println(matcher.group());
            xx.add(matcher.group());
        }

        return xx;

    }

    public static Set<String> FindSybaseDatabase(String dataString) {
        Set<String> xx = new HashSet<String>();

        Pattern pattern = Pattern.compile("\\w*\\.\\.");
        Matcher matcher = pattern.matcher(dataString);
        while (matcher.find()) {
            // System.out.println(matcher.group());
            xx.add(matcher.group().replace(".", ""));
        }

        return xx;

    }

    public static Set<String> findTablesFromInsert(String dataString) {

        String x = dataString;

        x = x.replaceAll("\n", " ");
        x = x.replaceAll("/\\*.*\\*/", " ");
        System.out.println(x);
        x = x.replaceAll("(?i)insert (?i)into ", "~~");
        System.out.println(x);
        x = x.replaceAll(" .*?~~", "~~");
        x = x.replaceAll(" .*?$", "~~");
        System.out.println(x);
        List<String> items = Arrays.asList(x.split("~~"));
        return convertListToSet(items);
    }

    public static Set<String> findTablesFromQuery(String dataString) {

        String x = dataString;
        x = x.replaceAll("\n", " ");
        x = x.replaceAll("^.*? select ", " select ");
        x = x.replaceAll("(?i)select .*? from ", "~~");
        x = x.replaceAll(" from ", "~~");
        x = x.replaceAll(" join ", "~~");
        x = x.replaceAll("(?i)where .*?\\(", "~~");
        x = x.replaceAll("\\).*?\\(", "~~");
        x = x.replaceAll("~~.*?(?i) from ", "~~");
        x = x.replaceAll("(?i)select .*? from ", "~~");
        x = x.replaceAll(" ", "");
        x = x.replaceAll("\\)~", "~");
        x = x.replaceAll("\\).*$", "~~");
        x = x.replaceAll("~~~~", "~~");
        List<String> items = Arrays.asList(x.split("~~"));
        return convertListToSet(items);

    }

    public static void getSybaseStoredProcs(Connection conn) throws SQLException {

        String query = "SELECT u.name as name1, o.name, c.text FROM sysusers u, syscomments c, sysobjects o "
                + "WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid  ORDER BY o.id, c.colid";

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            String name1 = rs.getString("name1");
            String name = rs.getString("name");
            String txt = rs.getString("text");

            System.out.println(name1 + ", " + name + ", " + txt);
        }

    }

    public static void writeListToCSV(List<String[]> stringList, String fullFilePath) throws Exception {
        try {
             
            CSVWriter writer = new CSVWriter(new FileWriter(fullFilePath));
            Boolean includeHeaders = true;

            writer.writeAll(stringList, includeHeaders);
            writer.close();

        } catch (Exception e) {
            System.out.println(e);
            // logger.error("Exception " + e.getMessage());
        }
    }

    private static boolean compareRow(String[] row1, String[] row2) {
        if (row1 == null || row2 == null) {
            return false;
        }
        if (row1.length == row2.length) {
            for (int i = 0; i < row1.length; i++) {
                if (!row1[i].toLowerCase().toString().equals(row2[i].toString().toLowerCase())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private static List<String[]> readCSV(String csvPath) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        List<String[]> results = reader.readAll();
        reader.close();
        return results;
    }

    public static Map<Integer, String> outputBinary(Connection conn, String path, String tableName, String columnName, String columnType)
        throws FileNotFoundException, SQLException, IOException
    {
        Map<Integer, String> results = new HashMap<Integer, String>();
        Statement stmt = conn.createStatement();
        String query = "SELECT " + columnName + " FROM " + tableName;

        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            Blob blob = rs.getBlob(columnName);
            InputStream in = blob.getBinaryStream();

            int length = in.available();
            byte[] blobBytes = new byte[length];
            in.read(blobBytes);

            String md5Hex = DigestUtils.md5Hex(blobBytes).toUpperCase();

            // change name here
            String dirPath = path + "/" + columnType + "/" + md5Hex;
            File blobFile = new File(dirPath);
            if(!blobFile.getParentFile().exists()){
                blobFile.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(blobFile);

            results.put(rs.getRow(), dirPath);
            fos.write(blobBytes);
            fos.close();
        }

        rs.close();
        stmt.close();
        return results;
    }

    /**
     * place holder logic to download image
     * 
     * @param conn
     * @param primaryKey
     * @throws IOException
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public static void downloadImage(Connection conn, String tableName, String columnName, int primaryKey, String filePath)
            throws FileNotFoundException, SQLException, IOException {

        Statement stmt = conn.createStatement();
        String query = "SELECT " + columnName + "  FROM " + tableName + " WHERE pid = " + primaryKey;
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            File blobFile = null;
            blobFile = new File(filePath);

            Blob blob = rs.getBlob(columnName);
            InputStream in = blob.getBinaryStream();

            int length = in.available();
            byte[] blobBytes = new byte[length];
            in.read(blobBytes);

            FileOutputStream fos = new FileOutputStream(blobFile);
            fos.write(blobBytes);
            fos.close();
            rs.close();
            stmt.close();

        }

    }

    /**
     * place hold to upload images
     * 
     * @param conn
     * @param file
     * @param uniqueid
     * @throws SQLException
     * @throws IOException
     */
    public static void uploadImage(Connection conn, String filep, int uniqueid) throws SQLException, IOException {
        File file = new File(filep);

        String filename = file.getName();
        int length = (int) file.length();

        FileInputStream filestream = null;

        filestream = new FileInputStream(file);

        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        // String query = "UPDATE assignment SET instructions_file = ?, instructions_filename = ? WHERE a_key = " + uniqueid;
        String query = "INSERT INTO blobtest (pid, img) VALUES (2, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setBinaryStream(1, filestream, length);
        //ps.setString(2, filename);
        ps.execute();
        ps.close();
        stmt.close();
        filestream.close();
    }

    /**
     * Place holder for hdf5
     * 
     * @throws HDF5Exception
     * @throws NullPointerException
     * @throws HDF5LibraryException
     * @throws IOException
     */
    public void hdf5load(String fileName)
            throws HDF5LibraryException, NullPointerException, HDF5Exception, IOException {

        DataOutputStream file = new DataOutputStream(new FileOutputStream("result.bin"));
        int data[][], datasetID, dataspaceID, i, j, rows, fileID;
        fileID = H5.H5Fopen(fileName, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);

        datasetID = H5.H5Dopen(fileID, "my_dataset");
        // we assume that the file was opened
        // previously
        dataspaceID = H5.H5Dget_space(datasetID);
        rows = (int) (H5.H5Sget_simple_extent_npoints(dataspaceID) / 1024);
        data = new int[rows][1024];
        H5.H5Dread(datasetID, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                HDF5Constants.H5P_DEFAULT, data);
        // fn_order(data); // call hypothetical function that orders "data" in an
        // ascending way
        for (i = 0; i < rows; i++)
            for (j = 0; j < 1024; j++)
                file.writeInt(data[i][j]);
        file.close();
        H5.H5Sclose(dataspaceID);
        H5.H5Dclose(datasetID);
    }

    /**
     * 
     */

    public static void writeToExcel(List<String> columnNames, Map<String, Object[]> excel_data, String sheetName,
            String fullFilePath) throws Exception {

        /* Create Workbook and Worksheet objects */
        HSSFWorkbook new_workbook = new HSSFWorkbook(); // create a blank workbook object
        HSSFSheet sheet = new_workbook.createSheet(sheetName); // create a worksheet with caption score_details
        int columnCount = columnNames.size();

        /* Load data into logical worksheet */
        Set<String> keyset = excel_data.keySet();
        int rownum = 0;
        // Header Row
        Row r = sheet.createRow(rownum);
        for (int i = 1; i <= columnCount; i++) {

            r.createCell(i - 1).setCellValue(columnNames.get(i - 1));

        }
        rownum++;
        for (String key : keyset) { // loop through the data and add them to the cell
            Row row = sheet.createRow(rownum++);
            Object[] objArr = excel_data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Double)
                    cell.setCellValue((Double) obj);
                else
                    cell.setCellValue((String) obj);
            }
        }

        FileOutputStream output_file = new FileOutputStream(new File(fullFilePath)); // create XLS file
        new_workbook.write(output_file);// write excel document to output stream
        output_file.close(); // close the file
        new_workbook.close();
    }

    /**
     * Given list of columns , findex the index location of it in the header row
     * 
     * @param keyColumns
     * @param headerRow
     * @return
     */
    public static List<Integer> findColumnIndex(List<String> keyColumns, String[] headerRow) {

        List<Integer> headerIndex = new ArrayList<>();
        int ii = 0;
        for (String col : keyColumns) {

            int jj = 0;
            for (String headerCol : headerRow) {
                ii++;
                if (col.toLowerCase().equals(headerCol.toLowerCase())) {
                    headerIndex.add(jj);
                }
            }
            ii++;
        }
        return headerIndex;

    }

    /**
     * Extract each keycolumn and makeit key, and the rest of the columns data
     * 
     * @param headerIndex
     * @param headerRow
     * @return
     */
    public static HashMap<String, String> fillHashMap(List<Integer> headerIndex, List<String[]> csv, String algorithm) {
        HashMap<String, String> mapCSVdata = new HashMap<>();
        for (String[] row : csv) {
            String hashKey = "";
            String data = "";
            for (Integer keyColIndex : headerIndex) {
                hashKey = hashKey + row[keyColIndex];
                for (int i = 0; i < row.length; i++) {
                    Integer iii = i;
                    
                    if (!headerIndex.contains(iii)) {
                        data = data + row[i];
                    }

                }
                String md5Hex = DigestUtils.md5Hex(data).toUpperCase();
                if (algorithm.toLowerCase().equals("hash")) {

                    mapCSVdata.put(hashKey, md5Hex);

                } else {
                    mapCSVdata.put(hashKey, data);
                }
            }
        }

        return mapCSVdata;
    }

    public static void compareCSV(String firstCSV, String secondCSV, String outFile, List<String> primaryColumn,
    String reportHeader,String algorithm) throws Exception {
        String[]  headersCSV1, headersCSV2;
        List<String[]> csv1, csv2, results;
        // System.out.println(primaryColumn);
        // outputHeaders = new String[] { "File1", "File2", "Reason", "Primary Column" };
        // results = new ArrayList<String[]>() {
        //     {
        //         add(outputHeaders);
        //     }
        // };
        csv1 = readCSV(firstCSV);
        csv2 = readCSV(secondCSV); 

        headersCSV1 = csv1.get(0);
        headersCSV2 = csv2.get(0);
 
        csv1.remove(0);
        csv2.remove(0);

        List<Integer> headerIndex1 = findColumnIndex(primaryColumn, headersCSV1);
        List<Integer> headerIndex2 = findColumnIndex(primaryColumn, headersCSV2);

        // Build array of primarykey column index locations

        if (!compareRow(headersCSV1, headersCSV2)) {
            throw new Exception("Headers do not match");
        }

        HashMap<String, String> mapCSVdata1 = fillHashMap(headerIndex1, csv1, algorithm);
        HashMap<String, String> mapCSVdata2 = fillHashMap(headerIndex2, csv2, algorithm);
        HashMap<String, String[]> descrpencyMap = new HashMap<>();

        for (Map.Entry<String, String> entry : mapCSVdata1.entrySet()) {
            String keyCSV1 = entry.getKey();
            String valCSV1 = entry.getValue();
            String valCSV2 = mapCSVdata2.get(keyCSV1);
            if (!valCSV1.equals(valCSV2)) {
                // add to final table
                String [] xxx = {valCSV1,valCSV2};
                descrpencyMap.put(keyCSV1, xxx);
            }

        }

        for (Map.Entry<String, String> entry : mapCSVdata2.entrySet()) {
            String keyCSV2 = entry.getKey();
            String valCSV2 = entry.getValue();
            String valCSV1 = mapCSVdata1.get(keyCSV2);
            if (!valCSV2.equals(valCSV1)) {
                String []xxx = {valCSV1,valCSV2};
                descrpencyMap.put(keyCSV2, xxx);
            }

        }
        results = new ArrayList<>();
         
        results.add(reportHeader.split(","));
        for (Map.Entry<String, String[]> row : descrpencyMap.entrySet()) {
            String key = row.getKey();
            String [] val = row.getValue();
             
            String [] xxx={key,val[0],val[1]};
            results.add(xxx);

        }

        // System.out.println(primaryColumn + "--------xxx------------");
        // List<String> headers = Arrays.asList(headersCSV1).stream().map(s ->
        // s.toLowerCase())
        // .collect(Collectors.toList());
        // System.out.println(headers + "--------------------");
        // for (String column : primaryColumn) {
        // int index = headers.indexOf(column.toLowerCase());
        // if (index == -1) {
        // String missing = "Missing in header ";
        // results.add(new String[] { missing, missing, missing, column });
        // } else {
        // int xx = 0;
        // /** Fore each Row in Csv1 */
        // for (String[] row1 : csv1) {
        // boolean found = false;
        // /** For Each Row in Csv2 */
        // int ii = 0;
        // String[] xxx = null;
        // for (String[] row2 : csv2) {
        // ii++;
        // xx = csv2.size();
        // if (row1[index].equals(row2[index])) {
        // if (!compareRow(row1, row2)) {
        // for (int i = 0; i < row1.length; i++) {
        // if (!row1[i].toLowerCase().equals(row2[i].toLowerCase())) {
        // results.add(new String[] { row1[i], row2[i],
        // "Values mismatch on column:" + headers.get(i), column });
        // System.out.println("Values didn't match for key: " + column);
        // }
        // }
        // }
        // found = true;
        // /** Race condition caused by this removed withe for each above... */
        // // csv2.remove(row2);
        // xxx = row2;
        // break;
        // }
        // }
        // if (!found) {
        // results.add(new String[] { row1[index], "missing", "Values missing", column
        // });
        // csv1.remove(row1);
        // } else {
        // csv2.remove(xxx);
        // }
        // }
        // }
        // }
        writeListToCSV(results, outFile);
    }

        public static void freeWayMigrate(Connection srcDbConn,List<Connection> trgConns,List<String> tableNames) throws SQLException {
    
        Statement stmt =  srcDbConn.createStatement();

        for (String tableName : tableNames){
            String sql="select * from "+tableName;
            

        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData metadata = rs.getMetaData();

        int columnCount = metadata.getColumnCount();
        List<Integer> imageColIndex = new ArrayList<Integer>();
        List<Integer> stringColIndex = new ArrayList<Integer>();
        String[] allColumnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            String type = metadata.getColumnTypeName(i);
            String columnName = metadata.getColumnName(i);
          
            // For now; later create custom enum. "image" isn't supported by JAVA
            if (type.equals("image")) {
                imageColIndex.add(i);
            } else {
                stringColIndex.add(i);
            }
            allColumnNames[i - 1] = columnName;
        }

        List<String[]> data = new ArrayList<String[]>();
  /*************************************** */
        //build the insert
        String columnsComma = String.join(",", allColumnNames);
        String columnsQuestion=  "?";
        for (int jj=0;jj<columnCount;jj++){
            if (jj>0)
                columnsQuestion=  columnsQuestion + ",?";
        }
        String sqlInsert = "INSERT INTO "+tableName+" ("+allColumnNames+") VALUES ("+columnsQuestion+")";
        List<Statement> trgStmnts = new ArrayList<>();
        List<PreparedStatement> trgPrep = new ArrayList<>();
        trgConns.forEach(conn->{
            conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            trgPrep.add( conn.prepareStatement(sql));

            });
            
/*************************************** */
        int ii=0;
        while (rs.next()) {
            ii++;
            String[] row = new String[columnCount];
             
            for (int stringIdx : stringColIndex) {
                row[stringIdx - 1] = rs.getString(stringIdx);
                for (PreparedStatement ps: trgPrep){
                    ps.setString(stringIdx, row[stringIdx - 1]);
                     
                }
            }
            for (int imgIdx : imageColIndex) {
                byte[] dataBytes = rs.getBytes(imgIdx);
                for (PreparedStatement ps: trgPrep){
                    ps.setBytes(imgIdx, dataBytes);
                     
                } 
            } 

            
            for (PreparedStatement ps: trgPrep){
                ps.addBatch();
            } 


            data.add(row);
            if (Math.floorMod(ii, 1000)==0){
                //Execute the batch every 1000 rows
                for (PreparedStatement ps: trgPrep){
                    ps.executeBatch();
                    
                } 
                System.out.println("Records Dumped: "+ii);
            }
        
        }
        for (Statement trgStmnt: trgStmnts){
            trgStmnt.executeBatch();
            
            trgStmnt.close();
        }

    }

        }

    }
}
