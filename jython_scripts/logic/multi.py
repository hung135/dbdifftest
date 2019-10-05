import threading
import time
import DataUtils
from java.util.concurrent import Callable

class FreeWay(Callable):
    def __init__(self, con, targets, table, query, batchSize, truncate):
        self.con = con
        self.targets = targets
        self.table = table
        self.query = query
        self.batchSize = batchSize
        self.truncate = truncate

        self.started = None
        self.completed = None
        self.result = None
        self.thread_used = None
        self.exception = None

    def __str__(self):
        if self.exception:
             return "[%s] error %s in %.2fs" % \
                (self.thread_used, self.exception, self.completed - self.started, )
        elif self.completed:
            return "[%s] moved in %.2fs" % \
                (self.thread_used,self.completed - self.started, )
        elif self.started:
            return "[%s]started at %s" % \
                (self.thread_used, self.started)
        else:
            return "[%s] not yet scheduled" % \
                (self.thread_used)

    def call(self):
        self.thread_used = threading.currentThread().getName()
        self.started = time.time()
        try:
            print(self.con, self.targets, self.query, self.table)
            DataUtils.freeWayMigrateMulti(self.con, self.targets, self.query, self.table, self.batchSize, self.truncate)
        except Exception as ex:
            self.exception = ex
        self.completed = time.time()
        return self