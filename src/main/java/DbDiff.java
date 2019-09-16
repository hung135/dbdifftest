
import java.util.Collection;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class DbDiff {
    public static final Map<String, String> env = System.getenv();

    public static void main(String[] args) throws ParseException {
        String sybasePassword = env.get("SYBASE_PASSWORD");

        Options options = new Options();
        options.addOption("p", "print", false, "Send print request to printer.")
                .addOption("g", "gui", false, "Show GUI Application").addOption("f", "file", false, "File")
                .addOption("n", true, "No. of copies to print");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CLITester", options);

        // This should be all you need to get a jdbc connection
        try {
            DbConn sybaseConn = new DbConn(DbConn.DbType.SYBASE, "sa", sybasePassword, "dbsybase", "5000", "master");
            DbConn oracleConn = new DbConn(DbConn.DbType.ORACLE, "system", "Docker12345", "dboracle", "1542", "dev");
        } catch (Exception e) {
            System.out.println(e);

        }
    }
}
