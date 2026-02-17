package ru.danilarassokhin.statistic.kafka;

import static ru.danilarassokhin.statistic.config.KafkaStreamsConfig.ACCUMULATOR_PUNCTUATION_DEFAULT_DURATION;
import static ru.danilarassokhin.statistic.config.KafkaStreamsConfig.STORE_NAME_PLAYER_DAMAGE;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.danilarassokhin.messaging.dto.event.PlayerDealDamageEventDto;
import ru.danilarassokhin.statistic.util.NullableAccumulator;

/**
 * {@link AccumulatingPunctuationProcessor} который складывает весь урон для каждого игрока.
 */
@Component
@Scope("prototype")
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
