CREATE DATABASE IF NOT EXISTS pe;

USE pe;

CREATE TABLE user_queue(handle VARCHAR(255), done TINYINT(1) DEFAULT 0, updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(handle), INDEX doneindex (done));

CREATE TABLE user_interests(handle VARCHAR(255), json MEDIUMTEXT NOT NULL, updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(handle));

alter table user_queue add column type varchar(1) not null after handle;

 alter table user_queue modify column handle varchar(600) not null;
