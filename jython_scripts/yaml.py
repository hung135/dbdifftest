import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import YamlParser

from objects.database import Database

# Notes:
# jython is python 2.7.1 
# It doesn't support list comp

def readyaml():
    parser = YamlParser() # NOT THREAD SAFE
    parsed_yaml = parser.readyaml()

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


if __name__ == "__main__":
    readyaml()