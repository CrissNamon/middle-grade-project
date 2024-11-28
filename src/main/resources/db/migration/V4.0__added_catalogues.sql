CREATE SCHEMA IF NOT EXISTS game;

SET search_path TO game;

CREATE TABLE IF NOT EXISTS catalogue_dungeon(
  id serial primary key,
  code text not null unique,
  health integer not null,
  check(health > 0)
);

INSERT INTO catalogue_dungeon(code, health) VALUES('SEWERS', 2);