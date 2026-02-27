package ru.danilarassokhin.statistic.config;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.danilarassokhin.messaging.dto.event.EventDto;
import ru.danilarassokhin.messaging.dto.event.EventType;
import ru.danilarassokhin.messaging.dto.event.PlayerDealDamageEventDto;
import ru.danilarassokhin.messaging.dto.event.BossSpawnedSystemEventDto;
import ru.danilarassokhin.statistic.kafka.PlayerDealDamageEventAccumulator;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

  public static final String BRANCH_NAME_PLAYER_DAMAGE = "player-damage";
  public static final String BRANCH_NAME_BOSS_SPAWNED = "boss-spawned";
  public static final String STORE_NAME_PLAYER_DAMAGE = "player-damage-store";
  public static final Duration ACCUMULATOR_PUNCTUATION_DEFAULT_DURATION = Duration.ofSeconds(10);

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Value(value = "${app.events.topic}")
  private String eventsTopic;

  @Value(value = "${app.events.topic}-")
  private String branchPrefix;

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kStreamsConfig(
      Serde<String> stringSerde,
      Serde<EventDto> gameEventDtoSerde,
      @Value("${spring.application.name}") String applicationName
  ) {
    Map<String, Object> props = new HashMap<>();
    props.put(APPLICATION_ID_CONFIG, applicationName);
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass().getName());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, gameEventDtoSerde.getClass().getName());
    return new KafkaStreamsConfiguration(props);
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }

  @Bean(name = STORE_NAME_PLAYER_DAMAGE)
  public StoreBuilder<KeyValueStore<String, Double>> playerDamageStoreBuilder(
      StreamsBuilder streamsBuilder
  ) {
    return Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(STORE_NAME_PLAYER_DAMAGE),
        Serdes.String(),
        Serdes.Double()
    );
  }

  @Bean
  @Primary
  public Serde<EventDto> eventDtoSerde(ObjectMapper objectMapper) {
    return new JsonSerde<>(EventDto.class, objectMapper);
  }

  @Bean
  @Primary
  public Serde<String> stringSerde() {
    return Serdes.String();
  }

  @Bean("topology")
  public Map<String, KStream<?, ?>> topology(
      StoreBuilder<KeyValueStore<String, Double>> playerDamageStoreBuilder,
      Serde<String> stringSerde,
      Serde<EventDto> eventDtoSerde,
      StreamsBuilder streamsBuilder,
      ObjectProvider<PlayerDealDamageEventAccumulator> accumulatorObjectProvider
  ) {
    streamsBuilder.addStateStore(playerDamageStoreBuilder);
    var branches = streamsBuilder
        .stream(eventsTopic, Consumed.with(stringSerde, eventDtoSerde))
        .split(Named.as(branchPrefix))
        .branch((key, value) -> value.getType() == EventType.PLAYER_DEAL_DAMAGE, Branched.as(BRANCH_NAME_PLAYER_DAMAGE))
        .branch((key, value) -> value.getType() == EventType.SYSTEM_EVENT_BOSS_SPAWNED, Branched.as(BRANCH_NAME_BOSS_SPAWNED))
        .noDefaultBranch();
    var playerDamageStream = branches.get(branchPrefix + BRANCH_NAME_PLAYER_DAMAGE)
        .mapValues(eventDto -> (PlayerDealDamageEventDto) eventDto)
        .process(() -> accumulatorObjectProvider.getObject(), STORE_NAME_PLAYER_DAMAGE);
    var bossSpawnedStream = branches.get(branchPrefix + BRANCH_NAME_BOSS_SPAWNED)
        .mapValues(eventDto -> (BossSpawnedSystemEventDto) eventDto);
    return Map.of(
        branchPrefix + BRANCH_NAME_PLAYER_DAMAGE, playerDamageStream,
        branchPrefix + BRANCH_NAME_BOSS_SPAWNED, bossSpawnedStream
    );
  }

  @Bean
  public KStream<String, Double> playerDamageStream(
      @Qualifier("topology") Map<String, KStream<String, ?>> branches
  ) {
    return (KStream<String, Double>) branches.get(branchPrefix + BRANCH_NAME_PLAYER_DAMAGE);
  }

  @Bean
  public KStream<String, BossSpawnedSystemEventDto> bossSpawnedStream(
      @Qualifier("topology") Map<String, KStream<String, ?>> branches
  ) {
    return (KStream<String, BossSpawnedSystemEventDto>) branches.get(branchPrefix + BRANCH_NAME_BOSS_SPAWNED);
  }
}
