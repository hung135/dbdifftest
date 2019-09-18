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
    parsed_yaml = parser.readyaml(yaml)

    database_objects, tasks = [], []
    for key in parsed_yaml:
        if key == "tasks":
            tasks = [val for val in parsed_yaml[key]]
        else:
            database_objects.append(Database(parsed_yaml[key]))

    print("\nDatabase objects found:")
    for obj in database_objects:
        print("\t{0}".format(str(obj)))

    print("\nTasks found:")
    for task in tasks:
       print("\t{0}".format(task))

    print("Executing sysbase")
    execute_sql_test_sysbase(database_objects)


def execute_sql_test_sysbase(database_objects):
    for base in database_objects:
        if base.dbtype == "SYBASE":
            sysBaseConnection = DbConn.DbType.SYBASE
            db = DbConn(sysBaseConnection, base.user, base.password, base.host, base.port, base.database_name);
            result = db.queryToList(SELECTQUERY)
            print(result)

def parse_cli():
    parser = argparse.ArgumentParser(description='Process a yaml file')
    parser.add_argument("-y", help="Location of the yaml file")
    args = parser.parse_args()
    return args 

def execute(args):
    databases = readyaml(args.yaml)
    print(databases)

if __name__ == "__main__":
    args = parse_cli()
    execute(args)