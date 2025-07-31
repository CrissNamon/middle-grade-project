CREATE SCHEMA IF NOT EXISTS game;

SET search_path TO game;

CREATE TABLE IF NOT EXISTS mail(
  id uuid not null primary key default gen_random_uuid(),
  email text not null,
  text text not null,
  is_processed boolean not null default false
);