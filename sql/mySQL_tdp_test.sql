DROP TABLE IF EXISTS job_results;
CREATE TABLE job_results (
	result_id BIGINT UNIQUE NOT NULL,
	md5 CHAR(32) UNIQUE NOT NULL,
	cdate TIMESTAMP NOT NULL DEFAULT now(),
	request_obj BLOB NOT NULL,
	status INT NOT NULL,
	note TEXT,
	PRIMARY KEY (result_id)
);

DROP TABLE IF EXISTS pub_results;
CREATE TABLE pub_results (
	result_id BIGINT NOT NULL,
	md5 CHAR(32) UNIQUE NOT NULL,
	cdate TIMESTAMP NOT NULL DEFAULT now(),
	request_obj BLOB NOT NULL,
   	result_data LONGBLOB NULL,
	PRIMARY KEY (result_id, md5)
);

DROP TABLE IF EXISTS marked_records;
CREATE TABLE marked_records (
	result_id BIGINT NOT NULL,
	mark CHAR(100),
	PRIMARY KEY (result_id,mark),
	FOREIGN KEY (result_id) REFERENCES pub_results(result_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS marked_jobs;
CREATE TABLE marked_jobs (
	id SERIAL PRIMARY KEY,
	result_id BIGINT NOT NULL,
	mark CHAR(100),
	FOREIGN KEY (result_id) REFERENCES job_results(result_id) ON DELETE CASCADE
);


DROP TABLE IF EXISTS failed_jobs;
CREATE TABLE failed_jobs (
	md5 CHAR(32) NOT NULL,
	cdate TIMESTAMP NOT NULL DEFAULT now(),
	request_obj BLOB NOT NULL,
	note TEXT
);

delete from pub_results;
delete from pub_results where not result_id in (select result_id from marked_records where mark='generic');
select result_id from pub_results;
select DATE(cdate) from pub_results where result_id in 
		(select result_id from marked_records where mark='generic')
		 and DATE(cdate) = DATE(NOW()); /* or  DATE(cdate) = "2011-09-01"  */
delete from pub_results where result_id in 
		(select result_id from marked_records where mark='generic') 
		and DATE(cdate) = "2011-09-01";		
delete from pub_results where not result_id in 
		(select result_id from marked_records where mark='generic')
		and DATE(cdate) < "2012-01-11";
select count(*) from pub_results where DATE(cdate) < "2012-01-01";

delete from pub_results where result_id = "1307038882826";

insert into pub_results (result_id,md5,request_obj,result_data) values (1234567,"one","","");
insert into marked_records (result_id,mark) values (1234567,"generic");



drop PROCEDURE copy_pub_results;
delimiter //
CREATE PROCEDURE copy_pub_results()
Begin
  Declare done INT DEFAULT FALSE;
  DECLARE _md5 CHAR(32);
  DECLARE _cdate TIMESTAMP;
  DECLARE _status INT;
  DECLARE _result_id BIGINT;
  DECLARE _request_obj BLOB;
  DECLARE cur CURSOR FOR SELECT result_id,md5,cdate,request_obj FROM pub_results;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO _result_id,_md5,_cdate,_request_obj;
    IF done THEN
      LEAVE read_loop;
    END IF;
    INSERT INTO job_results (result_id,md5,cdate,request_obj,status,note) VALUES (_result_id,_md5,_cdate,_request_obj,2,null);
  END LOOP;
  CLOSE cur;
END//
delimiter ;
