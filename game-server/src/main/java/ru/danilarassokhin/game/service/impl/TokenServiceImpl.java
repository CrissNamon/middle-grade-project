package ru.danilarassokhin.game.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import ru.danilarassokhin.game.exception.AuthException;
import ru.danilarassokhin.game.model.dto.TokenResponseDto;
import ru.danilarassokhin.game.service.TokenService;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class TokenServiceImpl implements TokenService {

  private static final JWSAlgorithm jwsAlg = JWSAlgorithm.RS256;

  private final ClientAuthentication clientAuthentication;
  private final URI tokenEndpoint;
  private final URI clientCallbackUri;
  private final IDTokenValidator validator;

  @Autofill
  public TokenServiceImpl(PropertiesFactory propertiesFactory) {
    var clientID = propertiesFactory.getAsString("oauth.client.id")
        .map(ClientID::new).orElseThrow();
    var clientSecret = propertiesFactory.getAsString("oauth.client.secret")
        .map(Secret::new).orElseThrow();
    var jwtSetUri = propertiesFactory.getAsString("oauth.jwt-set-uri")
        .map(this::createUrlSneaky).orElseThrow();
    var issuer = propertiesFactory.getAsString("oauth.issuer").orElseThrow();
    this.clientAuthentication = new ClientSecretBasic(clientID, clientSecret);
    this.clientCallbackUri = propertiesFactory.getAsString("oauth.client.callback-uri")
        .map(this::createUriSneaky).orElseThrow();
    this.tokenEndpoint = propertiesFactory.getAsString("oauth.token-uri")
        .map(this::createUriSneaky).orElseThrow();
    this.validator = new IDTokenValidator(new Issuer(issuer), clientID, jwsAlg, jwtSetUri);
  }

  @Override
  public TokenResponseDto exchangeCode(String code) {
    try {
      var codeGrant = new AuthorizationCodeGrant(new AuthorizationCode(code), clientCallbackUri);
      var request = new TokenRequest(tokenEndpoint, clientAuthentication, codeGrant);
      var tokenResponse = OIDCTokenResponseParser.parse(request.toHTTPRequest().send()).toSuccessResponse().getTokens();
      return new TokenResponseDto(tokenResponse.getAccessToken().toString(), tokenResponse.getRefreshToken().toString());
    } catch (IOException | com.nimbusds.oauth2.sdk.ParseException e) {
      throw new AuthException(e);
    }
  }

  @Override
  public boolean isValid(String token) {
    try {
      validator.validate(JWTParser.parse(token), null);
      return true;
    } catch (ParseException | BadJOSEException | JOSEException e) {
      return false;
    }
  }

  private URI createUriSneaky(String url) {
    return ThrowableOptional.sneaky(() -> new URI(url));
  }

  private URL createUrlSneaky(String url) {
    return ThrowableOptional.sneaky(() -> new URI(url).toURL());
  }
}
