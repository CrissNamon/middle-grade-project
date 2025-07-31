package ru.danilarassokhin.game.service.impl;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import ru.danilarassokhin.game.exception.AuthException;
import ru.danilarassokhin.game.service.AuthenticationService;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class AuthenticationServiceImpl implements AuthenticationService {

  private final Set<String> authorizationStates = Collections.synchronizedSet(new HashSet<>());

  private final ClientID clientID;
  private final URI clientCallbackUri;
  private final URI endpointUri;

  @Autofill
  public AuthenticationServiceImpl(PropertiesFactory propertiesFactory) {
    this.clientID = propertiesFactory.getAsString("oauth.client.id")
        .map(ClientID::new).orElseThrow();
    this.clientCallbackUri = propertiesFactory.getAsString("oauth.client.callback-uri")
        .map(it -> ThrowableOptional.sneaky(() -> new URI(it))).orElseThrow();
    this.endpointUri = propertiesFactory.getAsString("oauth.login-uri")
        .map(it -> ThrowableOptional.sneaky(() -> new URI(it))).orElseThrow();
  }

  @Override
  public String getLoginUrl() {
    var state = new State();
    var nonce = new Nonce();
    var request = new AuthenticationRequest
        .Builder(new ResponseType("code"), new Scope("openid", clientID.getValue()), clientID, clientCallbackUri)
        .endpointURI(endpointUri)
        .state(state)
        .nonce(nonce)
        .build();
    authorizationStates.add(state.getValue());
    return request.toURI().toString();
  }

  @Override
  public void validate(String state, String code) {
    if (!authorizationStates.contains(state)) {
      throw new AuthException("Unexpected authentication response");
    }
    authorizationStates.remove(state);
  }
}
