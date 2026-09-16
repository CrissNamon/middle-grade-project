-- Kafka engine table: reads raw JSON events from the game.event topic
CREATE TABLE IF NOT EXISTS game_events_queue
(
    id            UUID,
    dateTime      DateTime64(3),
    type          String,
    playerId      Nullable(Int32),
    damage        Nullable(Float64),
    bossId        Nullable(Int32)
)
ENGINE = Kafka
SETTINGS
    kafka_broker_list = 'localhost:9095',
    kafka_topic_list = 'game.event',
    kafka_group_name = 'clickhouse_game_events',
    kafka_format = 'JSONEachRow',
    kafka_num_consumers = 1;

-- MergeTree table: persistent queryable storage for game events
CREATE TABLE IF NOT EXISTS game_events
(
    id            UUID,
    dateTime      DateTime64(3),
    type          String,
    playerId      Nullable(Int32),
    damage        Nullable(Float64),
    bossId        Nullable(Int32)
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(dateTime)
ORDER BY (type, dateTime);

-- Materialized View: pipes data from Kafka engine table to MergeTree
CREATE MATERIALIZED VIEW IF NOT EXISTS game_events_mv TO game_events AS
SELECT
    id,
    dateTime,
    type,
    playerId,
    damage,
    bossId
FROM game_events_queue;
