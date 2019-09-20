import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.yaml.snakeyaml.Yaml;

import java.sql.Statement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyPair;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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
       if(row1 == null || row2 == null){
           return false;
       }
       if(row1.length == row2.length){
            for(int i=0; i < row1.length; i++){
                if(!row1[i].toLowerCase().toString().equals(row2[i].toString().toLowerCase())){
                    return false;
                }
            }
        } else {
            return false;
        }
       return true;
   }

    private static List<String[]> readCSV(String csvPath) throws IOException{
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        List<String[]> results = reader.readAll();
        reader.close();
        return results;
    }

    public static String[] getValueFromStringArray(List<String[]> csv, String key, int index){
        for (String[] value : csv) {
            if(value[index].equals(key.toLowerCase())){
                return value;
            }
        }
        return null;
    }

    public static void compareCSV(String firstCSV, String secondCSV, String outFile, HashMap<String,List<String>> primaryColumn) throws Exception {
        String[] headers = new String[]{"File1", "File2", "Column", "Key"};
        List<String[]> results = new ArrayList<String[]>(){{add(headers);}};
        List<String[]> csv1 = readCSV(firstCSV);
        List<String[]> csv2 = readCSV(secondCSV);
        String[] headerCSV1 = csv1.get(0);
        String[] headerCSV2 = csv2.get(0);
        csv1.remove(0);
        csv2.remove(0);

        if(!compareRow(headerCSV1, headerCSV2)){
            throw new Exception("Headers do not match");
        }
        for (Map.Entry<String, List<String>> keyPair : primaryColumn.entrySet()) {
            String[] toAdd;
            int index = Arrays.asList(headerCSV1).indexOf(keyPair.getKey());
            if(index == -1){
                String missing = "Missing in header ";
                toAdd = new String[]{missing, missing, keyPair.getKey()};
                results.add(toAdd);
                System.out.println(missing + keyPair.getKey());
            } else {
                for (String key : keyPair.getValue()) {
                    toAdd = new String[3];
                    boolean isEmpty = true;
                    String[] csv1Value = getValueFromStringArray(csv1, key, index);
                    String[] csv2Value = getValueFromStringArray(csv2, key, index);
                    
                    if(csv1Value == null && csv2Value == null){
                        toAdd = new String[]{"missing", "missing", keyPair.getKey(), key};
                    } else if((csv1Value != null || csv2Value != null) && !compareRow(csv1Value, csv2Value)){
                        // Data didn't match or something is null
                        if(csv1Value == null){ // doesn't have value
                            toAdd = new String[]{"missing", csv2Value[index], keyPair.getKey(), key};
                            System.out.println("CSV1 missing value for key: " + key);
                            isEmpty = false;
                        } else if (csv2Value == null){
                            toAdd = new String[]{csv1Value[index], "missing", keyPair.getKey(), key};
                            System.out.println("CSV2 missing value for key: " + key);
                            isEmpty = false;
                        } else {
                            for(int i=0; i < csv1Value.length; i++){
                                if(!csv1Value[i].toString().toLowerCase().equals(csv2Value[i].toString().toLowerCase())){
                                    toAdd = new String[]{csv1Value[i], csv2Value[i], keyPair.getKey(), key};
                                    System.out.println("Values didn't match for key: " + key);
                                    isEmpty = false;
                                    break;
                                }
                            }
                        }
                    }
                    if(!isEmpty){
                        results.add(toAdd);
                    }
                }
            }
        }
        writeListToCSV(results, outFile);
    }
}