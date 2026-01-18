package ru.danilarassokhin.game.service;

/**
 * Получает и хранит сервисный Jwt токен.
 */
public interface ClientAuthenticationService {

  /**
   * @return Актуальный Jwt токен
   */
  String getToken();

}
