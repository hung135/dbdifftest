CREATE TABLE bar (
		c1 NUMBER(10) IQ UNIQUE(65500);,
		c2 VARCHAR(20),
		c3 CLOB PARTITION (P1 IN Dsp11, P2 IN Dsp12,
		   P3 IN Dsp13),
		c4 DATE,
		c5 BIGINT,
		c6 VARCHAR(500) PARTITION (P1 IN Dsp21,
		   P2 IN Dsp22),
		PRIMARY KEY (c5) IN Dsp2) IN Dsp1
		) ; 

/* JDBC_EXCEL_POI_EXAMPLE.SQL */
create table oracle_to_excel
(
DEPT_ID NUMBER,
DEPT_NAME VARCHAR2(20)
)

insert into oracle_to_excel values(1,'Finance')
insert into oracle_to_excel values(2,'Marketing')
insert into oracle_to_excel values(3,'IT')

commit