CREATE SCHEMA IF NOT EXISTS notification;

SET search_path TO notification;

CREATE TABLE IF NOT EXISTS mail(
  id uuid not null primary key default gen_random_uuid(),
  email text not null,
  date_time timestamp not null default (now() at time zone 'utc'),
  is_processed boolean not null default false
);
