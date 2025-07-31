package ru.danilarassokhin.game.security;

import java.net.URI;
import java.util.Optional;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.service.TokenService;
import ru.danilarassokhin.server.HttpRequestFilter;
import ru.danilarassokhin.server.exception.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class JwtHttpFilter implements HttpRequestFilter {

  private static final String BEARER = "Bearer ";

  private final HttpSecurity httpSecurity;
  private final TokenService tokenService;

  @Override
  public void filter(FullHttpRequest httpRequest) {
    var path = URI.create(httpRequest.uri()).getPath();
    if (!httpSecurity.requiresAuth(path)) {
      return;
    }
    var isTokenValid = Optional.ofNullable(httpRequest.headers().get("Authorization"))
        .flatMap(this::getToken)
        .map(tokenService::isValid)
        .orElse(false);
    if (!isTokenValid) {
      throw new AuthenticationException();
    }
    log.debug("Request passed filter: {}", httpRequest);
  }

  private Optional<String> getToken(String header) {
    if (header.length() - 1 < BEARER.length()) {
      return Optional.empty();
    }
    return Optional.of(header.substring(BEARER.length()));
  }
}
