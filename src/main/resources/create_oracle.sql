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

    CREATE TABLE tablename
(NUM_PO NUMBER(19) DEFAULT AUTOINCREMENT,
MNT NUMBER(9) NULL,
QTY_PROD NUMBER(9) NULL,
NUMERIC(14); NULL
PRIMARY KEY (NUM_PO)
);