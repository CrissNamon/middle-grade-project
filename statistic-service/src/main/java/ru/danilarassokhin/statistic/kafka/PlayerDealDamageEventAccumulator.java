package ru.danilarassokhin.statistic.kafka;

import static ru.danilarassokhin.statistic.config.KafkaStreamsConfig.ACCUMULATOR_PUNCTUATION_DEFAULT_DURATION;
import static ru.danilarassokhin.statistic.config.KafkaStreamsConfig.STORE_NAME_PLAYER_DAMAGE;

import ru.danilarassokhin.messaging.dto.event.PlayerDealDamageEventDto;
import ru.danilarassokhin.statistic.util.NullableAccumulator;

public class PlayerDealDamageEventAccumulator extends AccumulatingPunctuationProcessor<PlayerDealDamageEventDto, String, Double> {

  public PlayerDealDamageEventAccumulator() {
    super(
        STORE_NAME_PLAYER_DAMAGE,
        record -> record.value().getPlayerId().toString(),
        record -> record.value().getDamage(),
        NullableAccumulator::sum,
        ACCUMULATOR_PUNCTUATION_DEFAULT_DURATION
    );
  }
}
