
DROP TASK SummarizeStaleEventsTask IF EXISTS;

DROP PROCEDURE SummarizeUniqueEvents IF EXISTS;

DROP PROCEDURE SummarizeStaleEvents IF EXISTS;

DROP TABLE events_pk IF EXISTS;

DROP TABLE event_totals IF EXISTS;

DROP STREAM unique_events IF EXISTS;

file remove_tables_example3.sql


