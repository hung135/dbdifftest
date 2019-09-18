import os

class Database(object):
    dbtype = None
    host = None
    port = None
    user = None
    password_envar = None
    database_name = None
    password = None

    def __init__(self, hashMap):
        self._set_values(hashMap)

    def _set_values(self, hashMap):
        for entry in hashMap.entrySet():
            if entry.key == "password_envar":
                setattr(self, entry.key, entry.value)
                setattr(self, "password", os.getenv(entry.value))

            setattr(self, entry.key, entry.value)

    def __repr__(self):
        return str(self.__dict__)