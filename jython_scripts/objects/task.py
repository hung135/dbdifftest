import sys
class Task(object):
    key = None
    parameters = {}
    e = None

    def __init__(self, hashMap, key):
        self.key = key
        self._set_values(hashMap)

    def _set_values(self, hashMap):
        try:
            params = {}
            for task in hashMap.entrySet():
                operation = {}
                for op_type in task.value.entrySet():
                    operation[op_type.key] = op_type.value
                params[task.key] = operation
            setattr(self, "parameters", params)
        except AttributeError as e:
            self.e = "There are no instruction set or formatting is bad\n\tDetailed\n\t{0}".format(e)

    def __repr__(self):
        return str(self.__dict__)