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


selectQuery = """
SELECT DISTINCT 
  sysobjects.name, 
  syscomments.text
, case 
 WHEN sysobjects.type = 'T' THEN 'TRIGGER'
 WHEN sysobjects.type = 'P' THEN 'PROCEDURE' 
 WHEN sysobjects.type = 'V' THEN 'VIEW' 
 ELSE 'UNKNOWN' END TYPE
FROM sysobjects INNER JOIN syscomments
  ON sysobjects.id = syscomments.id
WHERE sysobjects.type = 'P'
"""

# db =   DbConn(x, "sa", "myPassword", "dbsybase", "5000", "master")

# select = db.queryToList(selectQuery)
# name_proc = {}
# for sproc in select:
#   try:
#     if sproc:
#       if sproc[0] in name_proc.keys():
#         name_proc[sproc[0]] = name_proc[sproc[0]].append(sproc[1])
#       else:
#         name_proc[sproc[0]] = [sproc[1]]
#   except AttributeError:
#     print("Not found: {0}".format(sproc[0]))

# print(len(name_proc.keys()))
# print("".join(txt))
from Nums import DbType
from Utils import JLogger
x = DbType.SYBASE
logger = JLogger("/workspace/outme.log", "test")
db = DbConn(x, "sa", "myPassword", "dbsybase", "5000", "tempdb", logger)
# statement = """
# CREATE TABLE mockData(
#   pid int not null,
#   img image
# )
# """
# DataUtils.uploadImage(db.conn, "/workspace/samples/img.png", 
# statement = """
# SELECT * FROM blobtest
# """
# queryList = db.queryToList(statement)
# for y in queryList:
#   print(y)

# statement = """
# CREATE TABLE mockData1 (
#   record_id numeric(5,0) identity not null,
#   email_Id VARCHAR(500),
#   prefix VARCHAR(500),
#   name VARCHAR(500),
#   city VARCHAR(500),
#   state VARCHAR(500),
#   country VARCHAR(500),
# )
# """

statement = """ SELECT COUNT(*) FROM mockData1 """
#statement = """ DELETE FROM mockData1 """

# statement = """
# alter table mockData1
#     add record_id numeric(5,0) identity not null
# """

# statement = """
# SELECT * FROM mockData1 WHERE record_id BETWEEN 25 AND 300
# """

#x = DataUtils.downloadImage(db.conn, "blobtest", "img", 2, "/workspace/out.png")
# db.queryToCSVWithBinary(statement, "/workspace/output/")
x =db.queryToList(statement)
for y in x:
  print(y)
# db.loadCSV("mockData1", "/workspace/People_data1.csv")
# print("Executed")