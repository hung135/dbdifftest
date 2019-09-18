class Database(object):
    dbtype = None
    host = None
    port = None
    user = None
    password_envar = None
    database_name = None

    def __init__(self, hashMap):
        self._set_values(hashMap)

    def _set_values(self, hashMap):
        for entry in hashMap.entrySet():
            setattr(self, entry.key, entry.value)

    def __repr__(self):
        return str(self.__dict__)