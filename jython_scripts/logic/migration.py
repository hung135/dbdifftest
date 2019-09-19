import os
import DataUtils
 
import java.lang.Object
 
import csv


class TableDump(object):

    def __init__(self, dbConn,schemaOrOwner,writePath):
        path=os.path.abspath(writePath)
        print("All the tables in this DB")
        x=(dbConn.getTableNames(schemaOrOwner))
        for a in x:
            dbConn.queryToCSV('select * from {0}.{1}'.format(schemaOrOwner,a),path+a+'.csv')

    def __repr__(self):
        return str(self.__dict__)

class TableRowCount(object):
  
    def __init__(self, dbConn,schemaOrOwner,fileName):
        path=os.path.abspath(fileName)
        tableCount = []
        x=(dbConn.getTableNames(schemaOrOwner))
        for a in x:
            tmp=[]
            tmp.append(a)
            
            zz = dbConn.queryToList('select count(*) from {0}.{1}'.format(schemaOrOwner,a))
            for b in zz:
                for c in b:
                    tmp.append(c)
            
            tableCount.append(tmp)
      

        dataUtil= DataUtils()

  
        print("writing table count: ",path)
        header=["TableName","RowCount"]
        outPutTable = csv.writer(open(path, 'w'), delimiter=',',
                         quotechar='|')
        outPutTable.writerow(header)
        for row in tableCount:
            outPutTable.writerow(row)
       
 
 
    def __repr__(self):
        return str(self.__dict__)