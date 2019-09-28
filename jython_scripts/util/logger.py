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

from datetime import datetime
from org.apache.log4j import *

class Logger(object):
    def __init__(self, path):
        # https://jython.readthedocs.io/en/latest/appendixB/#logging
        self.logger = Logger.getLogger(datetime.)
        PropertyConfigurator.configure("C:\path_to_properties\log4j.properties")