import org.apache.log4j.Logger;
public abstract class LogWrapper {
    private final Logger logger = LoggerFactory.getLogger(getClass());
}