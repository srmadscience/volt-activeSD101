

DROP PROCEDURE ForwardUniqueEvents IF EXISTS;

DROP PROCEDURE SummarizeUniqueEvents IF EXISTS;

DROP PROCEDURE Oldest20 IF EXISTS;

DROP VIEW running_totals_by_user_view IF EXISTS;

DROP TABLE events_pk IF EXISTS;

DROP TABLE user_totals IF EXISTS;

DROP STREAM summarized_events_by_user IF EXISTS;

