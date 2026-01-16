package ru.danilarassokhin.game.model.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("expires_in") Instant expiresIn
) {

  public boolean isExpired() {
    return expiresIn.isBefore(Instant.now());
  }

}
