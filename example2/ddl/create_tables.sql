
DROP TABLE events_pk IF EXISTS;

CREATE TABLE events_pk
(userid bigint not null
,session_id bigint not null
,insert_date timestamp default now not null
,primary key (userid, session_id)) 
USING TTL 5 MINUTES ON COLUMN insert_date;

CREATE INDEX event_pk_ttl ON events_pk(insert_date);

DROP STREAM unique_events IF EXISTS;

CREATE STREAM unique_events
PARTITION ON COLUMN userid
EXPORT TO TOPIC unique_events_topic WITH KEY (userid)
(userid bigint not null
,session_id bigint not null
,insert_date timestamp default now not null) ;

