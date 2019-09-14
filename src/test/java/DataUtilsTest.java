
import junit.framework.TestCase;

public class DataUtilsTest extends TestCase {

    public void testLucky() {
        assertEquals(7, DataUtils.RegexExtract("aaa", " 'a' 'b' 'c'"));
    }
}
