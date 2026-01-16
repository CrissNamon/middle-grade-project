package ru.danilarassokhin.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;

@Configuration
public class ApplicationConfig {

  @Bean
  public SpelExpressionParser spelExpressionParser() {
    return new SpelExpressionParser();
  }

  @Bean
  public ParameterNameDiscoverer parameterNameDiscoverer() {
    return new DefaultParameterNameDiscoverer();
  }

  @Bean
  public ReactiveJwtDecoder reactiveJwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerLocation
  ) {
    return ReactiveJwtDecoders.fromIssuerLocation(issuerLocation);
  }

}
