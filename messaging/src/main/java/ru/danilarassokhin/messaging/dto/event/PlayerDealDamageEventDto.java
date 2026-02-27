package ru.danilarassokhin.messaging.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Событие о том, что игрок нанес урон босу.
 */
public class PlayerDealDamageEventDto extends PlayerEventDto {

  private final Double damage;

  @JsonCreator
  public PlayerDealDamageEventDto(
      @JsonProperty("id") UUID id,
      @JsonProperty("playerId") Integer playerId,
      @JsonProperty("dateTime") LocalDateTime dateTime,
      @JsonProperty("damage") Double damage
  ) {
    super(id, dateTime, EventType.PLAYER_DEAL_DAMAGE, playerId);
    this.damage = damage;
  }

  public Double getDamage() {
    return damage;
  }
}
