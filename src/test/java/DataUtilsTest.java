
import junit.framework.TestCase;

public class DataUtilsTest extends TestCase {

    public void testFunctions() {

        System.out
                .println(DataUtils.RegexExtract("\\w*.\\.\\.", " from db1..table1, db2..table2, db3..table3 'b' 'c'"));
        System.out.println(DataUtils.FindSybaseDatabase(
                " from db1..table1, db2..table2, db3..table3, db3.dbo.table4  where something = something '"));
        System.out.println(DataUtils.findTablesFromQuery(
                " dsfasdfa tdhei screasdf; asdfasdf before select select col1, (select 1 from xxx.xx) as z, aa,(select 1 from xxx.xx) from schema.table join x.table2   where x=(select abc from test.table1) as y "));
        System.out.println(DataUtils.findTablesFromInsert(
                " insert into xx.table1 (col1,col2) values (1,2),(2,3); insert into yy.table2 as select * from test.table1"));
    }
}
