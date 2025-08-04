package ru.danilarassokhin.game.security;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;
import ru.danilarassokhin.game.exception.AuthException;
import ru.danilarassokhin.server.HttpResponseInterceptor;

@Slf4j
@RequiredArgsConstructor
public class JwtStateHttpFilter implements HttpResponseInterceptor {

  private static final String STATE_PARAMETER = "state";

  private final Set<String> authorizationStates = Collections.synchronizedSet(new HashSet<>());

  private final HttpSecurity httpSecurity;

  @Override
  public void intercept(FullHttpRequest request, HttpResponse response) {
    var uri = URI.create(request.uri());
    var fullHttpResponse = (FullHttpResponse) response;
    if (httpSecurity.getLoginUrl().equals(uri.getPath())) {
      authorizationStates.add(getState(fullHttpResponse));
    }
    if (httpSecurity.getCodeUrl().equals(uri.getPath())) {
      if (!authorizationStates.remove(getState(request))) {
        throw new AuthException("Unknown state");
      }
    }
  }

  private String getState(FullHttpResponse httpResponse) {
    var content = httpResponse.content();
    var authenticationUrl = URI.create(content.toString(Charset.defaultCharset()));
    return new URIBuilder(authenticationUrl).getFirstQueryParam(STATE_PARAMETER).getValue();
  }

  private String getState(FullHttpRequest fullHttpRequest) {
    var authenticationUrl = URI.create(fullHttpRequest.uri());
    return new URIBuilder(authenticationUrl).getFirstQueryParam(STATE_PARAMETER).getValue();
  }
}
