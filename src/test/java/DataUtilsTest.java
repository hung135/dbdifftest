
import junit.framework.TestCase;

public class DataUtilsTest extends TestCase {

    public void testFunctions() {

        System.out
                .println(DataUtils.RegexExtract("\\w*.\\.\\.", " from db1..table1, db2..table2, db3..table3 'b' 'c'"));
    }
}
