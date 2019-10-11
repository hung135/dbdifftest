import os
import DataUtils
import copy
import datetime

from multi import FreeWay

import java.lang.Object
from java.util import HashMap
import logging
import jarray
import DbConn

import csv
import md5
from .parse_proc import *

class TableDump(object):
    """
    Dumps a table's rows
    
    Parameters
    ----------
    dbConn: DBConn (JAVA object)
        Database connection to use
    schemaOrOwner: str
        Schema or owner (i.e: dbo or myTable or dbo.myTable)
    writePath: str
        Output path
    rowlimit: int default=0
        Max limit to save
    """
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
    """
    Gets a table's row count and appends the result to CSV file
    
    Parameters
    ----------
    dbConn: DBConn (JAVA object)
        Database connection to use
    schemaOrOwner: str
        Schema or owner (i.e: dbo or myTable or dbo.myTable)
    fileName: str
        Output path
    Returns
    -------
    """
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
    """
    Gets a table's column information (types)
    
    Parameters
    ----------
    dbConn: DBConn (JAVA object)
        Database connection to use
    schemaOrOwner: str
        Schema or owner (i.e: dbo or myTable or dbo.myTable)
    fileName: str
        Output path
    """
    def __init__(self, dbConn, schemaOrOwner, fileName):
        tables = dbConn.getAllTableColumnAndTypes(schemaOrOwner)
        header=["TableName","Column","JDBCTYPE"]
        output = []
        output.append(header)
        for tbl in tables:
            tablename=""
            for col in tbl.TableInformation():
                x=[]

                y=col.split(',')
                
                
                if(len(y)==2): 
                    z=y[1].split(':')
                    x.append(tablename[0].upper())
                    #x.append(y[0].upper())
                    x.append(z[0].upper())
                    x.append(z[1].upper())
                 
                    output.append(x)
                else:
                    tablename=y
            #output.append(tbl.TableInformation())
        tblout = csv.writer(open(fileName, 'w'), delimiter=',', quotechar='"',lineterminator='\n',quoting=csv.QUOTE_ALL )
        for row in output:

            tblout.writerow(row)

class TableSampleCheckSum(object):
    """
    Saves a MD5 hash for sampleSize number of rows for a table
    
    Parameters
    ----------
    dbConn: DBConn (JAVA object)
        Database connection to use
    schemaOrOwner: str
        Schema or owner (i.e: dbo or myTable or dbo.myTable)
    writePath: str
        Output path
    sampleSize: int
        limit
    """
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


class TableLoadCsv(object):
    """
    TODO: This is in the works
    Bulk loading multiple CSV's into multiple tables
    
    Parameters
    ----------
    dbConn: DBConn (JAVA object)
        Database connection to use
    schemaOrOwner: str
        Schema or owner (i.e: dbo or myTable or dbo.myTable)
    file_tables: list(dict)
        tableName: table to load into
        filePath: path to CSV
    """
    def __init__(self, dbConn, schemaOrOwner, files_tables):
        for tableName, filePath in files_tables:
            print("Mocking Loading csv: ", tableName, filePath)

class QueryToCSV(object):
    """
    Executes a query to a CSV
    
    Parameters
    ----------
    dbConn: DBConn (JAVA object)
        Database connection to use
    create: list(dict)
        writePath: output
        sql: statment to execute
    """
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
    tableNames = []
    """
    Moves data from one Database's table to multiple other database connections
    
    Parameters
    ----------
    dbConn: DbConn (Custom JAVA class)
        database connection
    targetConnections: List(DbConn)
        databaes to target
    tableNames:
        target tables fro the connection
    batchsize: int
        max batch size before a manual garbage collection happens
    truncate: bool
        truncate the original tabl
    primary_column:
        primary column (primary key)
    threads:
        number of threads
    """
    def __init__(self, dbConn, targetConnections,tableNames,batchSize,truncate,primary_column=None,threads=None):
        self.tableNames = tableNames

        if (threads and primary_column) and threads is not 0:
            thread_nums = []
            for table, key in zip(tableNames, primary_column):
                table_min = int(dbConn.getAValue("SELECT min({0}) FROM {1}".format(key, table)))
                table_max = int(dbConn.getAValue("SELECT MAX({0}) FROM {1}".format(key, table)))

                per_thread = table_max/threads
                print("table_min: {0} | table_max: {1} | per_thread: {2}".format(table_min, table_max, per_thread))
                prev = 0
                for i in range(threads):
                    targets = []
                    copy_dbConn = self.cloner(dbConn)
                    for x in targetConnections:
                        targets.append(self.cloner(x))

                    prev+=per_thread
                    if i == 0:
                        query = "SELECT * FROM {0} WHERE {1} BETWEEN {2} AND {3}".format(table, key, table_min, per_thread)
                    elif i == threads-1: # Last thread takes care of the rest
                        query = "SELECT * FROM {0} WHERE {1} BETWEEN {2} AND {3}".format(table, key, (per_thread*i)+1, table_max)
                    else:
                        query = "SELECT * FROM {0} WHERE {1} BETWEEN {2} AND {3}".format(table, key, (per_thread*i)+1, prev)
                    print("thread {0} | {1}".format(i, query))
                    #self.identityManagement(targets)
                    fr = FreeWay(copy_dbConn, targets, table, query, batchSize, truncate)
                    thread_nums.append(fr)
            self.execute(thread_nums, threads)
        else:
            DataUtils.freeWayMigrate(dbConn, targetConnections, tableNames,batchSize,truncate)

    def cloner(self, con):
        """
        Clones a database connection and reconnects it
        
        Parameters
        ----------
        con: DbConn (Custom JAVA class)
            database connection
        Returns
        -------
        tmp: dbConn
            Cloned database connection
        """
        tmp = con.clone()
        tmp.reConnect()
        return tmp

    def identityManagement(self, targets, on=True):
        """
        If their is a primary key this set the IDENTITY_INSERT to on

        Parameters
        ----------
        targets: list(DbConn) (Custom JAVA class)
            database connection
        on: bool
            turn the IDENTITY_INSERT on or off
        """
        for targ in targets:
            for table in self.tableNames:
                targ.executeSql("SET IDENTITY_INSERT {0} {1}".format(table, "ON" if on else "OFF"))

    # https://github.com/jython/book/blob/master/src/chapter19/test_completion.py
    def execute(self, frees, threads):
        """
        Begins multithreaded execution

        Parameters
        ----------
        frees: list(Freeway)
            list of freeways to begin multithreaded execution
        threads:
            number of threads
        """
        from java.util.concurrent import Executors, ExecutorCompletionService
        pool = Executors.newFixedThreadPool(threads)
        ecs = ExecutorCompletionService(pool)
        for f in frees:
            ecs.submit(f)

        submitted = len(frees)
        while submitted > 0:
            result = ecs.take().get()
            print str(result)
            submitted -= 1

class quertyToCSVOutputBinary(object):
    """
    Queries all the binary object's hash, if found/contains, to a CS
    
    Parameters
    ----------
    dbConn: DbConn (Custom JAVA class)
        database connection
    sql: str
        Sql to execute
    writePath: str
        output
    rowlimit: int
        max number of rows to output
    """
    def __init__(self, dbConn, sql,  writePath,rowlimit=0):
        limit=""
        top=""
        modded_sql=sql
        if str(dbConn.dbType) == 'SYBASE' and rowlimit>0:
            top='TOP {} '.format(rowlimit)
            modded_sql=sql.replace("select ","select {}".format(TOP))
             
        elif str(dbConn.dbType) == 'ORACLE' and rowlimit>0:
            limit=' WHERE ROWNUM <= {2}'.format(rowlimit)
            if sql.contains(' where '):
                limit=limit.replace(" WHERE ", " AND ")
            modded_sql=sql+limit
        elif rowlimit>0:
            limit=' LIMIT {2}'.format(rowlimit)
            modded_sql=sql+limit

        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
        print(fqn,modded_sql)
        dbConn.quertyToCSVOutputBinary(modded_sql, fqn)
            
    def __repr__(self):
        return str(self.__dict__)

#todo..multiple sheets would be nice
class QueryToExcel(object):
    """
    Execute's a query to Excel
    
    Parameters
    ----------
    dbConn: DbConn (Custom JAVA class)
        database connection
    sql: str
        Sql to execute
    writePath: str
        output
    sheetName: str
        which sheetname to save the values to
    """
    def __init__(self, dbConn, sql,  writePath, sheetName="Sheet1"):
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
         
        dbConn.queryToExcel(sql,  sheetName,fqn)

class CompareCsv(object):
    """
    Compares two CSVs
    
    Parameters
    ----------
    csv1: str
        Path to CSV
    csv2: str
        Path to CSV
    outfile:
        path to save the results
    key_columns:
        columns to compare
    reportHeader:
        format of header
    algorithm:
        method which to compare, defaults to MD5 hash
    """
    def __init__(self, csv1, csv2, outfile, key_columns,reportHeader,algorithm="hash"):
        csv1=os.path.abspath(csv1)
        csv2=os.path.abspath(csv2)
        print("Comparying CSV: \n\t{}\n\t{}\nOutfile: {}".format(csv1,csv2,outfile))
         
        DataUtils.compareCSV(csv1, csv2, outfile, key_columns,reportHeader,algorithm)

class ParseProcs(object):
    """
    Parses information from stored procedures and outputs results
    
    Parameters
    ----------
    dbConn: DbConn (Custom JAVA class)
        database connection
    schemaOrOwner: str
        Schema or owner (i.e: dbo or myTable or dbo.myTable)
    writePath: str
        ouput
    """
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

