#JAVA ITEMS
import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import DataUtils
import jarray
import java
from java.util import HashMap

#DataUtils.compareCSV("/workspace/People_data.csv", "/workspace/People_data1.csv", "Prefix", "/workspace/output.csv")

#jars = jarray.array(["How you", "Be doin?"], java.lang.String)
#x = [jars, jars, jars]
#DataUtils.compareCSV2("/workspace/People_data.csv", "/workspace/People_data1.csv", x)

# primaryColumns = {"Id": jarray.array(["0", "1"], java.lang.String)}
primaryColumns = {"Id": ["0", "1", "3"]}
DataUtils.compareCSV("/workspace/People_data.csv", "/workspace/People_data1.csv", HashMap(primaryColumns))


