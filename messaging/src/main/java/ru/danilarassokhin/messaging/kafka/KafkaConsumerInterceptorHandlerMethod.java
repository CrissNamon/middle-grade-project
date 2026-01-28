package ru.danilarassokhin.messaging.kafka;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolverComposite;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

public class KafkaConsumerInterceptorHandlerMethod extends InvocableHandlerMethod {

  private final InvocableHandlerMethod delegate;
  private final KafkaConsumerInterceptor interceptor;

  public KafkaConsumerInterceptorHandlerMethod(InvocableHandlerMethod delegate, KafkaConsumerInterceptor interceptor, List<HandlerMethodArgumentResolver> resolvers) {
    super(delegate);
    this.delegate = delegate;
    this.interceptor = interceptor;
    setMessageMethodArgumentResolvers(new HandlerMethodArgumentResolverComposite().addResolvers(resolvers));
  }

  @Override
  public Object invoke(Message<?> message, Object... providedArgs) {
    try {
      interceptor.beforeArgsEvaluation(message, delegate.getBean(), delegate.getMethod(), providedArgs);
      var evaluatedArgs = getMethodArgumentValues(message, providedArgs);
      interceptor.beforeExecution(message, delegate.getBean(), delegate.getMethod(), evaluatedArgs);
      var result = delegate.invoke(message, evaluatedArgs);
      return interceptor.afterExecution(result, message, delegate.getBean(), delegate.getMethod(), evaluatedArgs);
    } catch (Throwable t) {
      return interceptor.onExecutionError(t);
    }
  }
}
