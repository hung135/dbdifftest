import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import DbConn
import DataUtils
import re
x = DbConn.DbType.SYBASE


selectQuery = """SELECT c.text FROM sysusers u, syscomments c, sysobjects o 
                WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid  ORDER BY o.id, c.colid""";
db =   DbConn(x, "sa", "myPassword", "dbsybase", "5000", "master");


x = db.queryToList(selectQuery);


def remove_comments(dataString):
  x = dataString
  x = re.sub("\n", " ", x)
  x = re.sub("\t", " ", x)
  x = re.sub("\/\*.*?\*\/", "", x)
  return x

def get_sybase_insert(dataString):
  x = remove_comments(dataString)
  print(x)
  x = re.findall(" insert \w+", x)
  return x
        #x = x.replace("^.*? select ", " select ");
        # x = x.replace("(?i)select .*? from ", "~~");
        # x = x.replace(" from ", "~~");
        # x = x.replace(" join ", "~~");
        # x = x.replace("(?i)where .*?\\(", "~~");
        # x = x.replace("\\).*?\\(", "~~");
        # x = x.replace("~~.*?(?i) from ", "~~");
        # x = x.replace("(?i)select .*? from ", "~~");
        # x = x.replace(" ", "");
        # x = x.replace("\\)~", "~");
        # x = x.replace("\\).*$", "~~");
        # x = x.replace("~~~~", "~~");
 
   

print(get_sybase_insert("""
sp_instmsg

/*
	if @description is not null
		delete sysmessages where error = @msg_num
*/
	if @msg_text is null
		return 2

	insert sysmessages values
		(@msg_num, 0, 0, @msg_text, null, null)
	insert table2
	return 0

dbo
sp_addconf
/*
** SP_ADDCONF
**	Insert configuration in sysconfigures when it does not exist.
**	When the configuration exists, but the status or parent are
**	different, sysconfigures is updated.
**	Parameters should match values in the file 
**	generic/source/utils
dbo
sp_addconf
/cfg_options
**
** Parameters:
**	@config		'config number' from cfg_options
**	@name		'config name' from cfg_options
**	@status		'status' from cfg_options
**	@parent		'parent' from cfg_options
**
*/ s

asdfasdf """))