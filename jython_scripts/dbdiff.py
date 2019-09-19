import sys
import argparse
import csv

#JAVA ITEMS
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import YamlParser
import DbConn
import DataUtils

from objects.database import Database
from objects.task import Task

# Notes:
# jython is python 2.7.1 
# It doesn't support list comp

# Task:
#   key : str
#   e : str
#   instruction : {instruction_key : operation }
#       op_key = database_connection.key
#       operation = sql/py to execute

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
    parser.add_argument("-y", help="Location of the yaml file", required=True)
    parser.add_argument("-t", help="Location of tasks folder", required=True)
    args = parser.parse_args()
    return args 

def task_execution(databases_connections, task_config):
    output = {} # task.key: {instruction_key: output, instruction_key: output}
    for task in task_config:
        print("Running task: {0}".format(task.key))
        if task.e:
            print("\tError occured in the task: {0}".format(task.e))
        else:
            output[task.key] = {}
            for instruction_key in task.instructions:
                if instruction_key in databases_connections.keys():
                    connection = databases_connections[instruction_key]
                    res = execute_sql_test_sysbase(connection, task.instructions[instruction_key])
                else:
                    res = "Task {0} with key {1} not found".format(task.key, instruction_key)
                output[task.key][instruction_key] = res
    return output

def export_results(rows, filename):
    with open(filename, "w+") as csvfile:
        writer = csv.writer(csvfile, delimiter=",")
        writer.writerows(rows)

def create_report(output):
    print("\n-------Results-------")
    for key in output:
        print("Task: {0}".format(key))
        for instruction in output[key]:
            rows = output[key][instruction]
            print("\t{0} results returned {1}".format(instruction, len(rows)))
            if len(rows) > 0:
                export_results(rows, "{0}-{1}.csv".format(key,instruction))

def execute(args):
    db_config, task_config = readyaml(args.y, args.t)
    databases_connections = create_db_connections(db_config)
    output = task_execution(databases_connections, task_config)
    create_report(output)


if __name__ == "__main__":
    args = parse_cli()
    execute(args)