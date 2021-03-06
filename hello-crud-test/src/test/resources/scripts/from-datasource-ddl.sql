DROP TABLE PRODUCT IF EXISTS;
CREATE TABLE PRODUCT (
	ID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
	PSKU VARCHAR(25) NOT NULL,
	NAME VARCHAR(25) NOT NULL,
	DESCRIPTION VARCHAR(50) ,
	constraint PSKU_UN unique (PSKU)
) ;

DROP TABLE USER IF EXISTS;
CREATE TABLE USER (
	ID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
	EMAIL VARCHAR(25) NOT NULL,
	FNAME VARCHAR(25),
	LNAME VARCHAR(25),
	constraint EMAIL_UN unique (EMAIL)
) ;