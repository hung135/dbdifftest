
import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")

from Nums import DbType
from Utils import JLogger
import DbConn
import DataUtils


def create_db_non_binary(db):
    statement = """
        CREATE TABLE mockData1 (
            record_id numeric(5,0) identity not null,
            email_Id VARCHAR(500),
            prefix VARCHAR(500),
            name VARCHAR(500),
            city VARCHAR(500),
            state VARCHAR(500),
            country VARCHAR(500),
        )
    """
    db.executeSql(statement)

def create_db_binary(db):
    statement = """
        CREATE TABLE blobtest(
            pid int not null,
            img image
        )
    """
    db.executeSql(statement)

def get_count(db, table):
    statement = """
        SELECT COUNT(*) FROM {0}
        """.format(table)
    result = db.queryToList(statement)
    for r in result:
        print(r)

def load_data(db, table, data_path):
    db.loadCSV(table, data_path)

def start():
    x = DbType.SYBASE
    logger = JLogger("/workspace/outme.log", "test")
    db_temp = DbConn(x, "sa", "myPassword", "dbsybase", "5000", "tempdb", logger)
    db_master = DbConn(x, "sa", "myPassword", "dbsybase", "5000", "master", logger)

    get_count(db_master, "mockData1")
    get_count(db_temp, "mockData1")
    # create_db_non_binary(db_master)
    # load_data(db, mockData1)
    # compare db_temp vs db_master

if __name__ == "__main__":
    start()