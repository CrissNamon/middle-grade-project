CREATE SCHEMA IF NOT EXISTS game;

SET search_path TO game;

CREATE TABLE IF NOT EXISTS player(
  id serial primary key,
  name varchar(20) not null,
  money integer not null default 0,
  experience integer not null default 0,
  level integer not null default 1,
  constraint ux_player_name unique(name)
);

CREATE TABLE IF NOT EXISTS dungeon(
  id serial primary key,
  level integer,
  code text not null,
  constraint ux_dungeon_level_code unique(level, code)
);

CREATE TABLE IF NOT EXISTS damage_log(
  id serial primary key,
  player_id integer not null references player(id),
  dungeon_id integer references dungeon(id),
  dateTime timestamp not null default (now() at time zone 'utc'),
  active boolean not null default true,
  damage integer not null
);