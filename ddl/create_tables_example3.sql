

--
-- Delete existing tables if they are present
--
file remove_tables_example3.sql

--
-- This table is used to spot duplicate session records
-- provided they arrive within a 5 minute window.
--
CREATE TABLE events_pk
(user_id bigint not null
,session_id bigint not null
,insert_date timestamp default now not null
,primary key (user_id, session_id)) 
USING TTL 30 MINUTES ON COLUMN insert_date;

--
-- To make things scale we hash based on user_id and spread
-- the table across multiple partitions, each of which has
-- a Volt processing engine
--
PARTITION TABLE events_pk ON COLUMN user_id;

--
-- We use "Time To Live" (TTL) to delete old records.
-- This needs an index to work
--
CREATE INDEX event_pk_ttl ON events_pk(insert_date);

--
-- Records which are unique are forwarded using this stream.
-- A Stream is like a table, but you can only INSERT into it.
--
CREATE STREAM summarized_events_by_user
PARTITION ON COLUMN user_id
EXPORT TO TOPIC unique_events_topic WITH KEY (user_id)
(user_id bigint not null
,insert_date timestamp default now not null
,event_value  bigint not null) ;

-- 
-- Table used for keeping track of running totals by user
--
CREATE TABLE event_totals 
(user_id bigint not null
,last_written timestamp default now not null
,total_value  bigint not null
,stale_date  timestamp
,primary key (user_id));

PARTITION TABLE event_totals ON COLUMN user_id;

CREATE INDEX event_totals_ix1 ON event_totals(stale_date);


-- 
-- The Java we run lives in this JAR file
--
LOAD CLASSES ../jars/voltSD101-example3.jar;
   
--
-- Tells Volt to look into the JARs it knows about and
-- create a PROCEDURE object from ForwardUniqueEvents.java.
--
CREATE PROCEDURE  
   PARTITION ON TABLE events_pk COLUMN user_id
   FROM CLASS SummarizeUniqueEvents;

