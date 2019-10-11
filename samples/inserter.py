
import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")

from Nums import DbType
from Utils import JLogger
import DbConn
import DataUtils

TABLE = "mockData1"

def create_db_non_binary(db):
    statement = """
        CREATE TABLE {0} (
            record_id int not null,
            email_Id VARCHAR(500),
            prefix VARCHAR(500),
            name VARCHAR(500),
            city VARCHAR(500),
            state VARCHAR(500),
            country VARCHAR(500),
        )
    """.format(TABLE)
    db.executeSql(statement)

def drop_table(db):
    statement = """
        DROP TABLE {0}
    """.format(TABLE)
    db.executeSql(statement)

def create_db_binary(db):
    statement = """
        CREATE TABLE blobtest(
            pid int not null,
            img image
        )
    """
    db.executeSql(statement)

def get_count(db):
    statement = """
        SELECT COUNT(*) FROM {0}
        """.format(TABLE)
    result = db.queryToList(statement)
    for r in result:
        print(r)

def load_data(db, data_path):
    db.loadCSV(TABLE, data_path)

def export_data(db, fileName):
    db.queryToCSV("SELECT * FROM {0} ORDER BY record_id".format(TABLE), "/workspace/outputs/{0}.csv".format(fileName))

def compare(fileName1, filename2):
    keys = ["name"]
    default = "/workspace/outputs/{0}.csv"
    DataUtils.compareCSV(default.format(fileName1), default.format(filename2), default.format("compare"), keys, "name,Temp,Master","hash")

def pipeline(db_master, db_temp):
    # drop_table(db_master)
    # drop_table(db_temp)

    create_db_non_binary(db_master)
    create_db_non_binary(db_temp)

    load_data(db_master, "/workspace/samples/mockData.csv")

    get_count(db_master)
    get_count(db_temp)

def start():
    x = DbType.SYBASE
    logger = JLogger("/workspace/outme.log", "test")
    db_temp = DbConn(x, "sa", "myPassword", "dbsybase", "5000", "tempdb", logger)
    db_master = DbConn(x, "sa", "myPassword", "dbsybase", "5000", "master", logger)

    pipeline(db_master, db_temp)
    # export_data(db_master, "master")
    # export_data(db_temp, "temp")
    # compare("temp", "master")
    # get_count(db_master)
    # get_count(db_temp)

if __name__ == "__main__":
    ## To use this application
    ## use the .devcontainer/data-generator/.py to create X number of records
    ## move into /workspaces/mockData.csv
    ## run app
    start()