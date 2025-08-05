package ru.danilarassokhin.notification.aspect;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.stereotype.Component;
import ru.danilarassokhin.notification.annotation.FeatureToggle;
import ru.danilarassokhin.notification.exception.MethodDisabledException;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

  private final ConfigurableBeanFactory configurableBeanFactory;

  private EmbeddedValueResolver embeddedValueResolver;

  @Pointcut("@annotation(ru.danilarassokhin.notification.annotation.FeatureToggle)")
  public void pointcut() {}

  @Before("pointcut()")
  public void checkFeatureToggle(JoinPoint joinPoint) {
    var signature = (MethodSignature) joinPoint.getSignature();
    var featureToggle = signature.getMethod().getAnnotation(FeatureToggle.class);
    var result = embeddedValueResolver.resolveStringValue(featureToggle.value());
    if (result == null || !result.equals("true") && !result.equals("false")) {
      throw new IllegalArgumentException("Invalid expression: " + featureToggle.value());
    }
    if (result.equals("false")) {
      throw new MethodDisabledException();
    }
  }

  @PostConstruct
  public void init() {
    embeddedValueResolver = new EmbeddedValueResolver(configurableBeanFactory);
  }

}
