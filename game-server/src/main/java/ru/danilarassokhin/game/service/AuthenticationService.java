package ru.danilarassokhin.game.service;

public interface AuthenticationService {

  String getLoginUrl();

  void validate(String state, String code);

}
