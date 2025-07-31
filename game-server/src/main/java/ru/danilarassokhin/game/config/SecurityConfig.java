package ru.danilarassokhin.game.config;

import java.util.Set;

import ru.danilarassokhin.game.security.HttpSecurity;
import ru.danilarassokhin.game.service.AuthenticationService;
import ru.danilarassokhin.game.service.TokenService;
import ru.danilarassokhin.game.service.impl.AuthenticationServiceImpl;
import ru.danilarassokhin.game.service.impl.TokenServiceImpl;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class SecurityConfig {

  @GameBean
  public AuthenticationService authenticationService(PropertiesFactory propertiesFactory) {
    return new AuthenticationServiceImpl(propertiesFactory);
  }

  @GameBean
  public TokenService tokenService(PropertiesFactory propertiesFactory) {
    return new TokenServiceImpl(propertiesFactory);
  }

  @GameBean
  public HttpSecurity httpSecurity() {
    return new HttpSecurity(Set.of("/api/**"));
  }

}
