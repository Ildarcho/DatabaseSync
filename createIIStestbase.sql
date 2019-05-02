CREATE TABLE jobs(
    ID int NOT NULL IDENTITY,
    DepCode char(20) NOT NULL,
    DepJob char(100) NOT NULL,
    Description char(255) NOT NULL,
    PRIMARY KEY (ID),
	UNIQUE (DepCode, DepJob)
);