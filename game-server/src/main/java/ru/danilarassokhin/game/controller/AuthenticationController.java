package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.service.AuthenticationService;
import ru.danilarassokhin.game.service.TokenService;
import ru.danilarassokhin.server.annotation.GetRequest;
import ru.danilarassokhin.server.annotation.PostRequest;
import ru.danilarassokhin.server.model.RequestEntity;
import ru.danilarassokhin.server.model.ResponseEntity;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final TokenService tokenService;

  @GetRequest(value = "/login", consumes = "text/plain", produces = "text/plain")
  public ResponseEntity login(RequestEntity request) {
    return ResponseEntity.ok(authenticationService.getLoginUrl());
  }

  @GetRequest(value = "/code", consumes = "text/plain")
  public ResponseEntity code(RequestEntity request) {
    String  code = request.getQueryParameter("code");
    authenticationService.validate(request.getQueryParameter("state"), code);
    return ResponseEntity.ok(tokenService.exchangeCode(code));
  }
}
