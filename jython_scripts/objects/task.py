import sys
import re
from datetime import datetime, date, timedelta

now = datetime.now()

RUNTIME_CONSTANTS = {"{today}": now.strftime("%Y-%m-%d") ,
   "{yesterday}": (date.today() - timedelta(days=1)).strftime("%Y-%m-%d"),
   "{thisyear}": now.strftime("%Y"),
   "{thismonth}": now.strftime("%Y-%m")
}

class Task(object):
    key = None
    parameters = {}
    e = None

    def __init__(self, hashMap, key):
        self.key = key
        self._set_values(hashMap)

    def _set_runtime(self, values, connection, qualifier=None, reg=True):
        runtime = {"{qualifier}": qualifier, "{taskName}": self.key, "{connection}": connection}
        runtime.update(RUNTIME_CONSTANTS)
        if reg:
            values = re.sub(r'\{(.*?)\}', lambda replace: runtime[replace.group()], values)
        else:
            for key in values:
                for inner in key:
                    key[inner] = re.sub(r'\{(.*?)\}',
                        lambda replace: runtime[replace.group()], key[inner])
        return values

    def _set_values(self, hashMap):
        try:
            params = {}
            for task in hashMap.entrySet():
                operation = {}
                for op_type in task.value.entrySet():
                    if op_type.key in ["targetConnections", "tableNames", "primary_column"]
                        operation[op_type.key] =  op_type.value
                    elif isinstance(op_type.value, (str, unicode)):
                        operation[op_type.key] =  self._set_runtime(op_type.value, task.key)
                    elif isinstance(op_type.value, (int, bool)):
                        operation[op_type.key] =  op_type.value
                    else:
                        operation[op_type.key] =  self._set_runtime(op_type.value, task.key, op_type.key, reg=False)
                params[task.key] = operation
            setattr(self, "parameters", params)
        except AttributeError as e:
            self.e = "There are no instruction set or formatting is bad\n\tDetailed\n\t{0}".format(e)

    def __repr__(self):
        return str(self.__dict__)