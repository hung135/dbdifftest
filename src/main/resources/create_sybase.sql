create schema  authorization dbo
   
    create table newauthors (
        au_id int not null,
        au_lname varchar (40) not null,
        au_fname varchar (20) not null)
    create table newtitleauthors (
        au_id int not null,
        title_id int not null)
    create view tit_auth_view
    as
        select au_lname, au_fname
            from newtitles, newauthors,
                newtitleauthors
        where 
        newtitleauthors.au_id = newauthors.au_id
        and 
        newtitleauthors.title_id =
             newtitles.title_id
    grant select on tit_auth_view to public 

    /* Adaptive Server has expanded all '*' elements in the following statement */ create view guest.testview as 
SELECT dbo.spt_values.name, dbo.spt_values.number, dbo.spt_values.[type], dbo.spt_values.ansi_w, 
dbo.spt_values.low, dbo.spt_values.high, dbo.spt_values.msgnum
FROM
	dbo.spt_values
