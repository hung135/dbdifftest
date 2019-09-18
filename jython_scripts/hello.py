import sys
sys.path.append("/workspace/target/DbTest-jar-with-dependencies.jar")
import DbConn
import DataUtils
x = DbConn.DbType.SYBASE


selectQuery = """SELECT c.text FROM sysusers u, syscomments c, sysobjects o 
                WHERE o.type = 'P' AND o.id = c.id AND o.uid = u.uid  ORDER BY o.id, c.colid""";
db =   DbConn(x, "sa", "myPassword", "dbsybase", "5000", "master");


x = db.queryToList(selectQuery);




print(DataUtils.findTablesFromInsert("""
sp_instmsg


	if @description is not null
		delete sysmessages where error = @msg_num

	if @msg_text is null
		return 2

	insert sysmessages values
		(@msg_num, 0, 0, @msg_text, null, null)

	return 0

dbo
sp_addconf
/*
** SP_ADDCONF
**	Insert configuration in sysconfigures when it does not exist.
**	When the configuration exists, but the status or parent are
**	different, sysconfigures is updated.
**	Parameters should match values in the file 
**	generic/source/utils
dbo
sp_addconf
/cfg_options
**
** Parameters:
**	@config		'config number' from cfg_options
**	@name		'config name' from cfg_options
**	@status		'status' from cfg_options
**	@parent		'parent' from cfg_options
**
*/
create or replace procedure sp_addconf
	@config	smallint
dbo
sp_addconf
,
	@name	varchar(255),
	@status	int,
	@parent	smallint,
	@value2	varchar(255)	= null,
	@value3	int 		= null,
	@value4	int		= null
as
	declare @value	int

	select @value = value 
		from syscurconfigs 
		where config = @config

	if @value is null
	begin
		/
dbo
sp_addconf
* 
		** No row in syscurconfigs, so this is not a valid config
		** for this server version, return.
		*/
		return 1
	end


	if exists (select * 
		from sysconfigures 
		where config = @config 
		and name = @name
		and status = @status
		and parent = @par
dbo
sp_addconf
ent)
	begin
		/*
		** Row with same config, name, status and parent already exists,
		** return.
		*/
		return 2
	end

	if exists (select * 
		from sysconfigures 
		where config = @config )
	begin
		/* update existing row */
		update sysconfigures
			set	
dbo
sp_addconf
name = @name,
				comment = @name,
				parent = @parent,
				status = @status
			where 	config = @config

		return 3
	end

	/* The option does not exist, thus insert a new row. */
	insert sysconfigures (
		config, value, comment, status, name, parent,
		v
dbo
sp_addconf
alue2, value3, value4)
	select  @config, @value, @name, @status, @name, @parent,
		@value2, @value3, @value4

	/* 
	** For options in 'Application Functionality' group, set
	** the value of new added option to be the same as the 
	** value of 'enable func
dbo
sp_addconf
tionality group'.
	*/
	if (@config != 543 and @parent = 49)
	begin
		select @value = value
		from sysconfigures
		where config = 543

		select config_admin(23, @config, @value, 0, null, null) 
	end

	return 0

dbo
sp_procxmode """))

# for a in x:
#     for z in a:
#         print(DataUtils.findTablesFromInsert(z))
    


