package ru.danilarassokhin.messaging.kafka;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

public class KafkaConsumerInterceptorMessageHandlerMethodFactory extends DefaultMessageHandlerMethodFactory {

  private final List<KafkaConsumerInterceptor> interceptors;
  private List<HandlerMethodArgumentResolver> resolvers;

  public KafkaConsumerInterceptorMessageHandlerMethodFactory(List<KafkaConsumerInterceptor> interceptors) {
    this.interceptors = interceptors;
  }

  @Override
  public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
    var handler = super.createInvocableHandlerMethod(bean, method);
    return interceptors.stream().filter(interceptor -> interceptor.filter(bean, method)).findFirst()
        .map(decorator -> (InvocableHandlerMethod) new KafkaConsumerInterceptorHandlerMethod(handler, decorator, resolvers))
        .orElse(handler);
  }

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();
    this.resolvers = initArgumentResolvers();
  }
}
