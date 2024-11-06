CREATE SCHEMA IF NOT EXISTS game;

set search_path to game;

CREATE TABLE IF NOT EXISTS player(
  id serial primary key,
  name varchar(20) not null,
  money integer not null default 0.0,
  experience integer not null default 0.0,
  constraint ux_user_name unique(name)
);