import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.opencsv.CSVWriter;

import java.util.List;
import java.util.Arrays;
import org.yaml.snakeyaml.Yaml;

import java.sql.Statement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static void writeStringListToCSV(List<String[]> stringList, String fullFilePath) throws Exception {

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

    /**
     * place holder logic to download image
     * 
     * @param conn
     * @param primaryKey
     * @throws IOException
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void downloadImage(Connection conn, String tableName, String columnName, int primaryKey, String filePath)
            throws FileNotFoundException, SQLException, IOException {

        Statement stmt = conn.createStatement();
        String query = "SELECT " + columnName + "  FROM " + tableName + " WHERE a_key = " + primaryKey;
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            File blobFile = null;
            blobFile = new File(rs.getString(filePath));

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
    public void uploadImage(Connection conn, File file, int uniqueid) throws SQLException, IOException {

        String filename = file.getName();
        int length = (int) file.length();

        FileInputStream filestream = null;

        filestream = new FileInputStream(file);

        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        String query = "UPDATE assignment SET instructions_file = ?, instructions_filename = ? WHERE a_key = "
                + uniqueid;
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setBinaryStream(1, filestream, length);
        ps.setString(2, filename);
        int rows = ps.executeUpdate();
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
     * 
     */

}
