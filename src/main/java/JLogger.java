import org.apache.log4j.*;

// https://jython.readthedocs.io/en/latest/appendixB/#logging
public class JLogger {
    public Logger logger;
    public JLogger(String path, String identifier){
        this.logger = Logger.getLogger(identifier);
    }
}