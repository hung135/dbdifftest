import sys
import os
import argparse
import csv
import datetime
# <<<<<<< HEAD
# import os 
# #windows make sure you use c:\\xxx\\file.jar
# jarpath=os.path.abspath("./DbTest-jar-with-dependencies.jar")
# #JAVA ITEMS
# sys.path.append(jarpath)
# # =======
import importlib

#JAVA ITEMS
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
sys.path.append("./DbTest-jar-with-dependencies.jar")
# Jtyhon needs to be implicit. os.path.sep is broken; hence why we do it like so:
# This is only for the packaged scripts
sys.path.append(
    os.path.abspath(
        "{0}{1}{2}".format(os.path.abspath(os.path.dirname(__file__)), os.sep, os.path.abspath("./DbTest-jar-with-dependencies.jar"))
    )
)
 
import YamlParser
import DbConn
import DataUtils
import JLogger

from objects.database import Database
from objects.task import Task

# Task:
#   key : str
#   e : str
#   instruction : {instruction_key : operation }
#       op_key = database_connection.key
#       operation = sql/py to execute

logger = None

def readyaml(db_yaml, task_yaml):
    parser = YamlParser() # NOT THREAD SAFE
    parsed_db_yaml = parser.ReadYAML(db_yaml)
    parsed_task_yaml = parser.ReadYAML(task_yaml)

    database_objects, tasks = [], []
 
    for key in parsed_db_yaml:
        database_objects.append(Database(parsed_db_yaml[key], key))
    for key in parsed_task_yaml:
        tasks.append(Task(parsed_task_yaml[key], key))
    return database_objects, tasks
 

# Returns {database.key: conn}
def create_db_connections(database_objects):
    databases_connections = {}
    for base in database_objects:
        baseConnection = DbConn.DbType.getMyEnumIfExists(base.dbtype)
        if baseConnection is not None:
            con = DbConn(baseConnection, base.user, base.password, base.host, base.port, base.database_name)
            databases_connections[base.key] = con
        else:
            baseConnection[base.key] = None
    return databases_connections
 

def execute_sql_test_sysbase(connection, task):
    if "sql" in task.keys():
        result = connection.queryToList(task["sql"])
        return result
    else:
        return "Operation not supported yet"

def parse_cli():
    parser = argparse.ArgumentParser(description='Process a yaml file')
    # parser.add_argument("-y", help="Location of the yaml file", required=True)
    # parser.add_argument("-t", help="Location of tasks folder", required=True)
    parser.add_argument("-v", help="Verbose logging", required=False)
    args = parser.parse_args()
    return args 

def task_execution(databases_connections, task_config):
    for task in task_config:
        print("Running task: {0}".format(task.key))
        if task.e:
            print("\tError occured in the task: {0}".format(task.e))
        else:
            for con_key in task.parameters:
                if con_key in databases_connections.keys():
                    try:
                        connection = databases_connections[con_key]

                        if "targetConnections" in task.parameters[con_key]:
                            target_keys = task.parameters[con_key]["targetConnections"].replace(" ", "").split(",")
                            targets = []
                            for key in target_keys:
                                if str(key) in databases_connections:
                                    target_con = databases_connections[key]
                                    targets.append(target_con)
                                else:
                                    raise Exception("{0} not in the connection list of: {1}".format(key, databases_connections))
                            # end
                            task.parameters[con_key]["targetConnections"] = targets # convert to list of target arrays

                        module = importlib.import_module("logic.migration")
                        class_ = getattr(module, task.key)
                        instance = class_(connection, **task.parameters[con_key])

                    except Exception as e:
                        print("Task {0} caused an exception:\n\t{1}".format(task.key, e))
                elif task.key == "CompareCsv":
                        module = importlib.import_module("logic.migration")
                        class_ = getattr(module, task.key)
                        instance = class_(**task.parameters[con_key])
                else:
                    log.debug("Task {0} with {1} not found".format(task.key, con_key))

def export_results(rows, filename):
    with open(filename, "w+") as csvfile:
        writer = csv.writer(csvfile, delimiter=",",lineterminator='\n',quotechar='"',quoting=csv.QUOTE_ALL)
        writer.writerows(rows)

def setup_logger(log_type):
    global logger
    logger = JLogger(os.path.join(os.path.dirname(os.path.realpath(__file__)), "configs", log_type), str(datetime.datetime.now())).logger
    print(logger)

def execute(args):
    if args.v:
        setup_logger(args.v)

    logger.debug("We be debuggin")
    # db_config, task_config = readyaml(args.y, args.t)
    # databases_connections = create_db_connections(db_config)
    # task_execution(databases_connections, task_config)
    # print("Task execution complete")


if __name__ == "__main__":
    args = parse_cli()
    execute(args)