package ru.danilarassokhin.notification.message;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.messaging.Message;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.danilarassokhin.messaging.kafka.KafkaConsumerInterceptor;
import ru.danilarassokhin.notification.annotation.ReactiveAuthorized;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaConsumerJwtInterceptor implements KafkaConsumerInterceptor<Mono<Void>> {

  private final SpelExpressionParser spelExpressionParser;
  private final ParameterNameDiscoverer parameterNameDiscoverer;
  private final ReactiveJwtDecoder reactiveJwtDecoder;

  @Override
  public boolean filter(Object bean, Method method) {
    return method.isAnnotationPresent(ReactiveAuthorized.class);
  }

  @Override
  public void beforeExecution(Message<?> message, Object bean, Method method, Object... args) {
    log.info("Found {} method with @ReactiveAuthorized inside {}", method, bean);
  }

  @Override
  public Object afterExecution(Mono<Void> result, Message<?> message, Object bean, Method method, Object... args) {
    log.info("Trying to write authentication to context for {}", method);
    log.info("Args: {}", args);
    var jwtToken = getJwtTokenFromMethod(bean, method, args);
    return reactiveJwtDecoder.decode(jwtToken)
        .flatMap(jwt -> {
          var auth = new JwtAuthenticationToken(jwt);
          auth.setAuthenticated(true);
          return ((Mono<?>) result).contextWrite(
              ReactiveSecurityContextHolder.withAuthentication(auth)
          );
        });
  }

  private String getJwtTokenFromMethod(Object bean, Method method, Object... args) {
    var reactiveAuthorized = method.getAnnotation(ReactiveAuthorized.class);
    var context = new MethodBasedEvaluationContext(bean, method, args, parameterNameDiscoverer);
    var value = spelExpressionParser.parseExpression(reactiveAuthorized.value()).getValue(context, byte[].class);
    if (value instanceof byte[] token) {
      return new String(token);
    }
    throw new RuntimeException("Token not found");
  }
}
