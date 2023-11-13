

LOAD CLASSES ../jars/voltSD101-example4.jar;
   
--
-- Tells Volt to look into the JARs it knows about and
-- create a PROCEDURE object from ForwardUniqueEvents.java.
--
CREATE PROCEDURE  
   PARTITION ON TABLE events_pk COLUMN user_id
   FROM CLASS SummarizeUniqueEvents;


CREATE PROCEDURE DIRECTED
   FROM CLASS SummarizeStaleEvents;  
   
CREATE TASK SummarizeStaleEventsTask
ON SCHEDULE  EVERY 1 SECONDS
PROCEDURE SummarizeStaleEvents
ON ERROR LOG 
RUN ON PARTITIONS;
