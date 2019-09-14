import java.util.Collection;
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
    public static Collection<String> RegexExtract(String regExString, String dataString) {
        System.out.println("xxxx");
        System.out.println("xxxxxxx");
        Collection<String> xx = null;

        String mydata = "some string with 'the data i want' inside";
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
        return xx;

    }
}
