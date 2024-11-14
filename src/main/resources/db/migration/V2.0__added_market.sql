CREATE SCHEMA IF NOT EXISTS game;

SET search_path TO game;

CREATE TABLE IF NOT EXISTS market(
  id serial primary key,
  item_code text not null,
  price integer not null,
  amount integer not null,
  constraint ux_market_item_price unique (item_code, price),
  CHECK (amount > -1)
);

CREATE TABLE IF NOT EXISTS player_item(
  id serial primary key,
  player_id integer references player(id),
  item_code text not null,
  amount integer not null,
  CHECK (amount > -1),
  constraint ux_player_item_id unique(player_id, item_code)
);