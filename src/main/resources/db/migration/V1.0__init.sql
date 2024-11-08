CREATE SCHEMA IF NOT EXISTS game;

set search_path to game;

CREATE TABLE IF NOT EXISTS player(
  id serial primary key,
  name varchar(20) not null,
  money integer not null default 0,
  experience integer not null default 0,
  level integer not null default 1,
  constraint ux_player_name unique(name)
);

CREATE TABLE IF NOT EXISTS dungeon(
  level serial primary key,
  code text not null
);

CREATE TABLE IF NOT EXISTS dungeon_log(
  id serial primary key,
  player_id integer not null references player(id),
  dateTime timestamp not null default (now() at time zone 'utc'),
  damage integer not null
);