

--
-- Delete existing tables if they are present
--
file remove_tables_example4.sql

LOAD CLASSES ../jars/voltSD101-example4.jar;

CREATE PROCEDURE DIRECTED
   FROM CLASS SummarizeStaleEvents;  
   
CREATE TASK SummarizeStaleEventsTask
ON SCHEDULE EVERY 10 SECONDS
PROCEDURE SummarizeStaleEvents
ON ERROR LOG 
RUN ON PARTITIONS;
