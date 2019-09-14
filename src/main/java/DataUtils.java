import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functions we need to write that process some data, put them here
 */
public class DataUtils {

    DataUtils() {
        System.out.println("Constructor called");
    }

    /**
     * Take a regex and a data string and extract all the data that matches the
     * regex and returns it as a collection of string
     * 
     * @param regExString
     * @param dataString
     */
    public static List<String> RegexExtract(String regExString, String dataString) {
        List<String> xx = new ArrayList<String>();

        Pattern pattern = Pattern.compile(regExString);
        Matcher matcher = pattern.matcher(dataString);
        while (matcher.find()) {
            // System.out.println(matcher.group());
            xx.add(matcher.group());
        }

        return xx;

    }

}
