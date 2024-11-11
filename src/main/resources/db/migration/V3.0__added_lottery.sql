CREATE SCHEMA IF NOT EXISTS game;

SET search_path TO game;

CREATE TABLE IF NOT EXISTS lottery(
  id serial primary key,
  player_id integer references player(id),
  constraint ux_lottery_player_id unique(player_id)
);