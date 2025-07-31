package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.TokenResponseDto;

public interface TokenService {

  TokenResponseDto exchangeCode(String code);

  boolean isValid(String token);

}
