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

def readyaml(db_yaml, task_yaml):
    parser = YamlParser() # NOT THREAD SAFE
    parsed_db_yaml = parser.ReadYAML(db_yaml)
    parsed_task_yaml = parser.ReadYAML(task_yaml)

    database_objects, tasks = [], []
    for key in parsed_db_yaml:
        database_objects.append(Database(parsed_db_yaml[key], key))
    for key in parsed_task_yaml:
            tasks = [val for val in parsed_task_yaml[key]]
    return database_objects, tasks

def create_db_connections(database_objects):
    databases_connections = {}
    for base in database_objects:
        baseConnection = DbConn.DbType.getMyEnumIfExists(base.dbtype)
        if baseConnection is not None:
            con = DbConn(baseConnection, base.user, base.password, base.host, base.port, base.database_name)
            databases_connections[base] = con
        else:
            baseConnection[base] = None
    return databases_connections

def execute_sql_test_sysbase(db):
    result = db.queryToList(SELECTQUERY)
    print(result)

def parse_cli():
    parser = argparse.ArgumentParser(description='Process a yaml file')
    parser.add_argument("-y", help="Location of the yaml file", required=True)
    parser.add_argument("-t", help="Location of tasks folder", required=True)
    args = parser.parse_args()
    return args 

def execute(args):
    db_config, task_config = readyaml(args.y, args.t)
    databases_connections = create_db_connections(db_config) # dic

    print("\nConnection stats:")
    print("\t{0} active connections:".format(len(databases_connections)))
    for key in databases_connections:
        print("\t\t{0} : {1}".format(key, databases_connections[key]))

if __name__ == "__main__":
    args = parse_cli()
    execute(args)