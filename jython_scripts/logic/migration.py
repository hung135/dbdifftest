import os
import DataUtils

import java.lang.Object
from java.util import HashMap

import csv
import md5


class TableDump(object):

    def __init__(self, dbConn, schemaOrOwner, writePath):
        path = os.path.abspath(writePath)
        if not os.path.exists(path):
            os.makedirs(path)

        x = (dbConn.getTableNames(schemaOrOwner))
        for a in x:
            fqn = os.path.join(path, a+'.csv')
            try:
                dbConn.queryToCSV(
                    'select * from {0}.{1}'.format(schemaOrOwner, a), fqn)
            except:
                pass

    def __repr__(self):
        return str(self.__dict__)


class TableRowCount(object):

    def __init__(self, dbConn, schemaOrOwner, fileName):
        path = os.path.abspath(fileName)
        tableCount = []
        x = (dbConn.getTableNames(schemaOrOwner))
        for a in x:
            tmp = []
            tmp.append(a)
            zz = []
            try:
                zz = dbConn.queryToList(
                    'select count(*) from {0}.{1}'.format(schemaOrOwner, a))
                for b in zz:
                    for c in b:
                        tmp.append(c)
                tableCount.append(tmp)
            except:
                tableCount.append([a, 'SQL Execute Error'])

        header = ["TableName", "RowCount"]
        outPutTable = csv.writer(open(path, 'w'), delimiter=',',
                                 quotechar='|')
        outPutTable.writerow(header)
        for row in tableCount:
            outPutTable.writerow(row)

    def __repr__(self):
        return str(self.__dict__)


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
                                         quotechar='|')
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
     
    def __init__(self, dbConn, sql,  writePath):
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
        dbConn.queryToCSV(sql, fqn)
            

    def __repr__(self):
        return str(self.__dict__)
#todo..multiple sheets would be nice
class QueryToExcel(object):
    def __init__(self, dbConn, sql,  writePath,sheetName="Sheet1"):
        directory = os.path.dirname(writePath)
        if not os.path.exists(directory):
            os.makedirs(directory)
        fqn = os.path.abspath(writePath)
         
        dbConn.queryToExcel(sql,  sheetName,fqn)

class CompareCsv(object):
    def __init__(self, csv1, csv2, outfile, key_columns):
        csv1=os.path.abspath(csv1)
        csv2=os.path.abspath(csv1)
        
        DataUtils.compareCSV(csv1, csv2, outfile, list(key_columns))
