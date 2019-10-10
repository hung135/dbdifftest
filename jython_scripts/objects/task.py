import sys
import re
from datetime import datetime, date, timedelta

now = datetime.now()

runtime_dict = {"{today}": now.strftime("%Y-%m-%d") ,
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

    def _set_runtime(self, values, qualifier, connection):
        runtime = {"{qualifier}": qualifier, "{taskName}": self.key, "{connection}": connection}
        runtime.update(runtime_dict)
        for key in values:
            for inner in key:
                key[inner] = re.sub(r'\{(.*?)\}',
                    lambda replace: runtime[replace.group()], key[inner])
        print(values)
        return values

    def _set_values(self, hashMap):
        try:
            params = {}
            for task in hashMap.entrySet():
                operation = {}
                for op_type in task.value.entrySet():
                    operation[op_type.key] =  self._set_runtime(op_type.value, op_type.key, task.key)
                params[task.key] = operation
        except AttributeError as e:
            self.e = "There are no instruction set or formatting is bad\n\tDetailed\n\t{0}".format(e)

    def __repr__(self):
        return str(self.__dict__)