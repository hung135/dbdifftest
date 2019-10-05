import os
import DataUtils

import java.lang.Object
from java.util import HashMap
import logging
import jarray
import DbConn
import Multithreading # custom package

import csv
import md5
from .parse_proc import *

class TableDump(object):

    def __init__(self, dbConn, schemaOrOwner, writePath,rowlimit=0):
        limit=""
        top=""
        if (str(dbConn.dbType) == 'SYBASE') and rowlimit>0:
            top='TOP {}'.format(rowlimit)
             
        if (str(dbConn.dbType) != 'SYBASE') and rowlimit>0:
            limit='LIMIT {}'.format(rowlimit)
        
        path = os.path.abspath(writePath)
        if not os.path.exists(path):
            os.makedirs(path)

        x = (dbConn.getTableNames(schemaOrOwner))
        for a in x:
            fqn = os.path.join(path, a+'.csv')
            try:
                dbConn.queryToCSV(
                    'select {2} * from {0}.{1} {3}'.format(schemaOrOwner, a, fqn,top,limit))
            except:
                pass

    def __repr__(self):
        return str(self.__dict__)


class TableRowCount(object):
    def __init__(self, dbConn, schemaOrOwner, fileName):
        path = os.path.abspath(fileName)
        tableCount = []
        x = (dbConn.getTableNames(schemaOrOwner))
        y = (dbConn.getViewNames(schemaOrOwner))
        for a in x:
            tmp = []
            tmp.append(a)
             
            try:
                zz = dbConn.getAValue(
                    'select count(*) from {0}.{1}'.format(schemaOrOwner, a))
                tmp.append(zz)
                tableCount.append(tmp)
            except:
                logging.exception(Exception)
                #print("Error Querying Table: {}\n{}".format(a,e))
                tableCount.append([a, 'SQL Execute Error'])
        for a in y:
            tmp = []
            tmp.append(a)
             
            try:
                zz = dbConn.getAValue(
                    'select count(*) from {0}.{1}'.format(schemaOrOwner, a))
                tmp.append(zz)
                tableCount.append(tmp)
            except:
                logging.exception(Exception)
                tableCount.append([a, 'SQL Execute Error'])
        header = ["TableName", "RowCount"]
        outPutTable = csv.writer(open(path, 'w'), delimiter=',',
                                 quotechar='"',lineterminator='\n',quoting=csv.QUOTE_ALL)
        outPutTable.writerow(header)
        for row in tableCount:
            outPutTable.writerow(row)

    def __repr__(self):
        return str(self.__dict__)

class TableInformation(object):
    def __init__(self, dbConn, schemaOrOwner, fileName):
        tables = dbConn.getAllTableColumnAndTypes(schemaOrOwner)
        header=["TableName","Index","Column"]
        output = []
        output.append(header)
        for tbl in tables:
            tablename=""
            for col in tbl.TableInformation():
                x=[]

                y=col.split(',')
                
                if(len(y)==2): 
                    x.append(tablename[0])
                    x.append(y[0])
                    x.append(y[1])
                 
                    output.append(x)
                else:
                    tablename=y
            #output.append(tbl.TableInformation())
        tblout = csv.writer(open(fileName, 'w'), delimiter=',', quotechar='"',lineterminator='\n',quoting=csv.QUOTE_ALL )
        for row in output:

            tblout.writerow(row)

class TableSampleCheckSum(object):
    def __init__(self, dbConn, schemaOrOwner, writePath, sampleSize):
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)

        x = (dbConn.getTableNames(schemaOrOwner))
        table_row_hash = []
        sql = 'select * from {0}.{1} limit {2}'

        if str(dbConn.dbType) == 'SYBASE':
            sql = 'select TOP {2} * from {0}.{1}'
        if str(dbConn.dbType) == 'ORACLE':
            sql = 'select * from {0}.{1} WHERE ROWNUM <= {2}'
        for a in x:

            try:
                sample = dbConn.queryToList(
                    sql.format(schemaOrOwner, a, sampleSize))

                m = md5.new()
                if len(sample) == 0:
                    table_row_hash.append([a, "Zero Rows"])
                for row in sample:
                    for col in row:
                        # adds each column to md5 hash algorith,
                        m.update(str(col))
                    table_row_hash.append([a, m.hexdigest()])
                #print("writing table sample Hash: ",writePath)
                header = ["TableName", "SampleDataHash"]

                outPutTable = csv.writer(open(writePath, 'w'), delimiter=',',
                                         quotechar='"',lineterminator='\n',quoting=csv.QUOTE_ALL)
                outPutTable.writerow(header)
                for row in table_row_hash:
                    outPutTable.writerow(row)
            except:
                table_row_hash.append([a, 'SQL Execute ERROR'])

    def __repr__(self):
        return str(self.__dict__)


# ToDos this in the works
class TableLoadCsv(object):
    def __init__(self, dbConn, schemaOrOwner, files_tables):
        for tableName, filePath in files_tables:

            print("Mocking Loading csv: ", tableName, filePath)

class QueryToCSV(object):
    def __init__(self, dbConn, create):
        for process in create:
            directory = os.path.dirname(process["writePath"])
            sql = process["sql"]
            if not os.path.exists(directory):
                os.makedirs(directory)
            fqn = os.path.abspath(process["writePath"])
            dbConn.queryToCSV(sql, fqn)
            
    def __repr__(self):
        return str(self.__dict__)

class moveDataToDatabases(object):
     def __init__(self, dbConn, targetConnections,tableNames,batchSize,truncate,threads=None, pk=None):
        if threads and threads is not 0 and pk:
            for table in tableNames:
                table_min = 0
                table_max = 10000
                # table_min = dbConn.getAValue("SELECT min({0}) FROM {1}".format(pk, table))
                # table_max = dbConn.getAValue("select max({0}) from {1}".format(pk, table))
                per_thread = table_max/threads
                prev = 0
                for i in range(threads):
                    prev+=per_thread
                    if i == 0:
                        query = "SELECT * FROM {0} WHERE {1} BETWEEN {2} AND {3}".format(table, pk, table_min, per_thread)
                    else:
                        query = "SELECT * FROM {0} WHERE {1} BETWEEN {2} AND {3}".format(table, pk, per_thread*i, prev)
                    print("{0} : {1}".format(i,query))
                    multi = Multithreading(dbConn, targetConnections, tableName, sql, batchSize, truncate)
        else:
            DataUtils.freeWayMigrate(dbConn, targetConnections,tableNames,batchSize,truncate)

class quertyToCSVOutputBinary(object):
    def __init__(self, dbConn, sql,  writePath,rowlimit=0):
        limit=""
        top=""
        if str(dbConn.dbType) == 'SYBASE' and rowlimit>0:
            top='TOP {}'.format(rowlimit)
             
        if str(dbConn.dbType) != 'SYBASE' and rowlimit>0:
            limit='LIMIT {}'.format(rowlimit)
            
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
        print(fqn,sql)
        dbConn.quertyToCSVOutputBinary(sql, fqn)
            
    def __repr__(self):
        return str(self.__dict__)

#todo..multiple sheets would be nice
class QueryToExcel(object):
    def __init__(self, dbConn, sql,  writePath, sheetName="Sheet1"):
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
         
        dbConn.queryToExcel(sql,  sheetName,fqn)

class CompareCsv(object):
    def __init__(self, csv1, csv2, outfile, key_columns,reportHeader,algorithm="hash"):
        csv1=os.path.abspath(csv1)
        csv2=os.path.abspath(csv2)
        print("Comparying CSV: \n\t{}\n\t{}\nOutfile: {}".format(csv1,csv2,outfile))
         
        DataUtils.compareCSV(csv1, csv2, outfile, key_columns,reportHeader,algorithm)

class ParseProcs(object):
    def __init__(self,dbConn,schemaOrOwner,writePath):
        
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
        total=[]
        objType={"Func":"F","Proc":"P","Trigger":"TR"
        
        ,"ExtProc":"XP"
        }
        for key in objType:

            oType=objType[key]
            x=dbConn.getSybaseObjNames(schemaOrOwner,oType)
            print("Parsing: ",key)
            for proc in x:
                
                ddl=dbConn.getSybaseCode(proc,oType) 
                queryfrom=""
                updatefrom=""
                if ddl is None:
                    queryfrom="error"
                    print("errror ",proc,ddl)
                else:
                    x = remove_comments(ddl)
                    queryfrom=get_querys(x)
                    updatefrom=get_updates(x)
                total.append([dbConn.databaseName,proc,key,queryfrom,updatefrom])
        v=dbConn.getViewNames(schemaOrOwner)
        if v is not None:
            for view in v:
                ddl=dbConn.getSybaseViewDDL(schemaOrOwner,view) 
                queryfrom=""
                if ddl is None:
                    queryfrom="error"
                else:
                    v = remove_comments(ddl)

                    queryfrom=get_querys(v)
                    
                    total.append([dbConn.databaseName,view,"VIEW",queryfrom,""])
             

        header = ["Database","Name","Type","Query","Update"]
        outPutTable = csv.writer(open(fqn, 'w'), delimiter=',',
                                quotechar='"',lineterminator='\n',quoting=csv.QUOTE_ALL)
        outPutTable.writerow(header)
        for row in total:
            outPutTable.writerow(row)
            #tmp = re.findall(" from \w+.*?where", x)
            #if len(tmp)>0: 
            #   total.append(filter_junk(tmp))
            #   tmp = re.findall(" insert into \w+", x)
            #   if len(tmp)>0: 
            #     total.append(tmp)
             
        # for a,b,c in x:
        #     tables=get_sybase_insert(c)
        #     queries=get_sybase_select(c)
        #     if (len(tables)>0 or len(queries)>0):
        #         print(a,b,tables,queries)
        print("Used Datbase: ",dbConn.databaseName)

