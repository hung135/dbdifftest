import os
class Database(object):
    """
    Python database object that will map to Java object

    Attributes
    ----------
    key: str
        Identifier for tasks (i.e: sys-dev)
    dbtype: str (DbConn.Enums)
        Database type
    host: str
        Database host
    port: int
        Database port
    user: str
        Database user
    password_envar: str
        environment variable to search for
    database_name: str
        name of DB
    password: str
        password for db
    """
    key = None
    dbtype = None
    host = None
    port = None
    user = None
    password_envar = None
    database_name = None
    password = None

    def __init__(self, hashMap, key):
        self._set_values(hashMap)
        self.key = key

    def _set_values(self, hashMap):
        """
        Sets each value from the connection's yaml to their respective values

        Parameters
        ----------
        hashMap: Java.HashMap
            Values from YAML
        """
        for entry in hashMap.entrySet():
            if entry.key == "password_envar":
                setattr(self, entry.key, entry.value)
                setattr(self, "password", os.getenv(entry.value))
            setattr(self, entry.key, entry.value)

    def __repr__(self):
        return str(self.__dict__)