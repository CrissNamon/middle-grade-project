package ru.danilarassokhin.notification.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.danilarassokhin.notification.annotation.ReactiveAuthorized;

@Aspect
@Component
@RequiredArgsConstructor
public class ReactiveAuthorizationAspect {

  private final SpelExpressionParser spelExpressionParser;
  private final ParameterNameDiscoverer parameterNameDiscoverer;
  private final ReactiveJwtDecoder reactiveJwtDecoder;

  @Pointcut("@annotation(ru.danilarassokhin.notification.annotation.ReactiveAuthorized)")
  public void pointcut() {}

  @Around("pointcut()")
  public Object contextWrite(ProceedingJoinPoint joinPoint) throws Throwable {
    var jwtToken = getJwtTokenFromMethod(joinPoint);
    var result = joinPoint.proceed(joinPoint.getArgs());
    if (result instanceof Mono) {
      return reactiveJwtDecoder.decode(jwtToken)
          .flatMap(jwt -> {
            var auth = new JwtAuthenticationToken(jwt);
            auth.setAuthenticated(true);
            return ((Mono<?>) result).contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(auth)
            );
          });
    }
    return result;
  }

  private String getJwtTokenFromMethod(ProceedingJoinPoint joinPoint) {
    var signature = (MethodSignature) joinPoint.getSignature();
    var reactiveAuthorized = signature.getMethod().getAnnotation(ReactiveAuthorized.class);
    var context = new MethodBasedEvaluationContext(joinPoint.getThis(), signature.getMethod(),
                                                   joinPoint.getArgs(), parameterNameDiscoverer);
    return spelExpressionParser.parseExpression(reactiveAuthorized.value())
        .getValue(context, String.class);
  }
}
