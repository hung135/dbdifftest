import sys
import re
 
]
def diable_oracle_table_constraint(dbConn, schemaName,tableName):
       sql="""begin
                for cur in (select owner, constraint_name , table_name 
                    from all_constraints
                    where owner = '{0}' and
                    TABLE_NAME = '{1}') loop
                execute immediate 'ALTER TABLE '||cur.owner||'.'||cur.table_name||' 
                MODIFY CONSTRAINT "'||cur.constraint_name||'" DISABLE ';
            end loop;
            end; """.format(schemaName,tableName)
        dbConn.exeCallableStmt(sql)

def enable_oracle_table_constraint(dbConn,schemaName,tableName):
    sql = """begin
                for cur in (select owner, constraint_name , table_name 
                    from all_constraints
                    where owner = '{0}' and
                    TABLE_NAME = '{1}') loop
                execute immediate 'ALTER TABLE '||cur.owner||'.'||cur.table_name||'
                MODIFY CONSTRAINT "'||cur.constraint_name||'" ENABLE ';
            end loop;
            end; """.format(schemaName,tableName)
    dbConn.exeCallableStmt(sql)