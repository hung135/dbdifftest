import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import DbConn
import DataUtils
import re

def remove_comments(dataString):
  x = dataString
  x = re.sub("\n", " ", x)
  x = re.sub("\t", " ", x)
  x = re.sub("\/\*.*?\*\/", "", x)
  return x
def filter_junk(dataString):
    filter_list=[
        ' insert into',
        ' insert in'
    ]
    total = []
    for a in dataString:
        if a not in filter_list:
            total.append(a)
    return total
def get_sybase_insert(dataString):
  total=[]
  x = remove_comments(dataString).lower()
  tmp = re.findall(" insert \w+", x)
  if len(tmp)>0: 
    total.append(tmp)
#   tmp = re.findall(" insert into \w+", x)
#   if len(tmp)>0: 
#     total.append(tmp)
  return total

def get_sybase_select(dataString):
  total=[]
  x = remove_comments(dataString).lower()
  tmp = re.findall(" from \w+.*?where", x)
  if len(tmp)>0: 
    total.append(filter_junk(tmp))
#   tmp = re.findall(" insert into \w+", x)
#   if len(tmp)>0: 
#     total.append(tmp)
  return total

x = DbConn.DbType.SYBASE


selectQuery = """SELECT distinct u.name as name1, o.name, c.text FROM sysusers u, syscomments c, sysobjects o 
                 WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid  ORDER BY o.id, c.colid""";
db =   DbConn(x, "sa", "myPassword", "dbsybase", "5000", "master");


x = db.queryToList(selectQuery);

for a,b,c in x:
    tables=get_sybase_insert(c)
    queries=get_sybase_select(c)
    if (len(tables)>0 or len(queries)>0):
        print(a,b,tables,queries)


      