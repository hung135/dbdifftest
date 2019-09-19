import sys
import argparse

#JAVA ITEMS
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import YamlParser
import DbConn
import DataUtils

from objects.database import Database

# Notes:
# jython is python 2.7.1 
# It doesn't support list comp
SELECTQUERY = """SELECT c.text FROM sysusers u, syscomments c, sysobjects o 
                WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid  ORDER BY o.id, c.colid""";

def readyaml(yaml):
    parser = YamlParser() # NOT THREAD SAFE
    parsed_yaml = parser.ReadYAML(yaml)

    print(parsed_yaml)
    database_objects, tasks = [], []
    for key in parsed_yaml:
        if key == "tasks":
            tasks = [val for val in parsed_yaml[key]]
            print(tasks)
        else:
            database_objects.append(Database(parsed_yaml[key]))

    print("\nDatabase objects found:")
    for obj in database_objects:
        print("\t{0}".format(str(obj)))

    print("\nTasks found:")
    for task in tasks:
       print("\t{0}".format(task))

    return database_objects

def create_db_connections(database_objects):
    connections = []
    for base in database_objects:
        baseConnection = DbConn.DbType.getMyEnumIfExists(base.dbtype)
        if baseConnection is not None:
            con = DbConn(baseConnection, base.user, base.password, base.host, base.port, base.database_name)
            connections.append(con)
    from logic.migration import TableDump,TableRowCount
    
    #x=TableDump(con,'dbo','/workspace/target/tmp')
    x=TableRowCount(con,'dbo' ,'/workspace/target/table_row_count.csv')

    return connections

def execute_sql_test_sysbase(db):
    result = db.queryToList(SELECTQUERY)
    print(result)

def parse_cli():
    parser = argparse.ArgumentParser(description='Process a yaml file')
    parser.add_argument("-y", help="Location of the yaml file")
    args = parser.parse_args()
    return args 

def execute(args):
    config = readyaml(args.y)
    databases_connections = create_db_connections(config)

    print("\nConnection stats:")
    print("\t{0} active connections:".format(len(databases_connections)))
    for db in databases_connections:
        print("\t\t{0}".format(db))

if __name__ == "__main__":
    args = parse_cli()
    execute(args)