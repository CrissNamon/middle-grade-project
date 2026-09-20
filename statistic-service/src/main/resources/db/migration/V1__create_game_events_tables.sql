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
    kafka_broker_list = '${kafka-bootstrap-servers}',
    kafka_topic_list = 'game.event',
    kafka_group_name = 'clickhouse_game_events',
    kafka_format = 'JSONEachRow',
    kafka_num_consumers = 1;

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

CREATE MATERIALIZED VIEW IF NOT EXISTS game_events_mv TO game_events AS
SELECT
    id,
    dateTime,
    type,
    playerId,
    damage,
    bossId
FROM game_events_queue;
