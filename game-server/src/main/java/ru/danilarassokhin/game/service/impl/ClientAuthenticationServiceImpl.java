package ru.danilarassokhin.game.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.model.response.JwtResponse;
import ru.danilarassokhin.game.service.ClientAuthenticationService;
import ru.danilarassokhin.server.exception.AuthenticationException;
import ru.danilarassokhin.util.PropertiesFactory;

@Slf4j
public class ClientAuthenticationServiceImpl implements ClientAuthenticationService {

  private final ObjectMapper objectMapper;
  private final HttpRequest httpRequest;

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final AtomicReference<JwtResponse> currentJwtReference = new AtomicReference<>();
  private final ReentrantLock lock = new ReentrantLock();

  public ClientAuthenticationServiceImpl(PropertiesFactory propertiesFactory,
                                         ObjectMapper objectMapper) {
    URI tokenUri = URI.create(propertiesFactory.getAsString("oauth.token-uri").orElseThrow());
    String clientId = propertiesFactory.getAsString("oauth.client.id").orElseThrow();
    String clientSecret = propertiesFactory.getAsString("oauth.client.secret").orElseThrow();
    HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(
        String.format("grant_type=client_credentials&client_id=%s&client_secret=%s", clientId, clientSecret)
    );
    this.objectMapper = objectMapper;
    this.httpRequest = HttpRequest.newBuilder()
        .uri(tokenUri)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(requestBody)
        .build();
  }

  @Override
  public String getToken() {
    var currentToken = currentJwtReference.get();
    if (currentToken == null || currentToken.isExpired()) {
      try {
        lock.lock();
        currentToken = currentJwtReference.get();
        if (currentToken == null || currentToken.isExpired()) {
          currentToken = retrieveToken();
          currentJwtReference.set(currentToken);
        }
      } finally {
        lock.unlock();
      }
    }
    return currentToken.accessToken();
  }

  private JwtResponse retrieveToken() {
    try {
      HttpResponse<String> response = httpClient.send(httpRequest,
                                                      HttpResponse.BodyHandlers.ofString());
      return objectMapper.readValue(response.body(), JwtResponse.class);
    } catch (InterruptedException | IOException e) {
      throw new AuthenticationException(e);
    }
  }
}
