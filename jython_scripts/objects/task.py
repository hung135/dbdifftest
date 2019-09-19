class Task(object):
    key = None
    instructions = {}
    e = None

    def __init__(self, hashMap, key):
        self._set_values(hashMap)
        self.key = key

    def _set_values(self, hashMap):
        try:
            for task in hashMap.entrySet():
                operation = {}
                for op_type in task.value.entrySet():
                    operation[op_type.key] = op_type.value
                self.instructions[task.key] = operation
        except AttributeError as e:
            self.e = "There are no instruction set or formatting is bad\n\tDetailed\n\t{0}".format(e)

    def __repr__(self):
        return str(self.__dict__)