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