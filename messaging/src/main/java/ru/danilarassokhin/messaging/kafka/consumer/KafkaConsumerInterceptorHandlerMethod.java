package ru.danilarassokhin.messaging.kafka.consumer;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolverComposite;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

public class KafkaConsumerInterceptorHandlerMethod extends InvocableHandlerMethod {

  private final InvocableHandlerMethod delegate;
  private final List<KafkaConsumerInterceptor> interceptors;

  public KafkaConsumerInterceptorHandlerMethod(
      InvocableHandlerMethod delegate,
      List<KafkaConsumerInterceptor> interceptors,
      List<HandlerMethodArgumentResolver> resolvers
  ) {
    super(delegate);
    this.delegate = delegate;
    this.interceptors = interceptors;
    setMessageMethodArgumentResolvers(new HandlerMethodArgumentResolverComposite().addResolvers(resolvers));
  }

  @Override
  public Object invoke(Message<?> message, Object... providedArgs) {
    try {
      executeInterceptorMethod(
          interceptor -> interceptor.beforeArgsEvaluation(message, delegate.getBean(), delegate.getMethod(), providedArgs),
          message, providedArgs
      );
      var evaluatedArgs = getMethodArgumentValues(message, providedArgs);
      executeInterceptorMethod(
          interceptor -> interceptor.beforeExecution(message, delegate.getBean(), delegate.getMethod(), evaluatedArgs),
          message, providedArgs
      );
      var result = delegate.invoke(message, evaluatedArgs);
      return interceptors.stream()
          .filter(interceptor -> interceptor.filter(message, delegate.getBean(), delegate.getMethod()))
          .reduce(result, (value, acc) -> acc.afterExecution(value, message, delegate.getBean(), delegate.getMethod(), evaluatedArgs), (prev, next) -> prev);
    } catch (Exception t) {
      executeInterceptorMethod(
          interceptor -> interceptor.onExecutionError(message, delegate.getBean(), delegate.getMethod(), t, providedArgs),
          message, providedArgs
      );
      throw new RuntimeException(t);
    }
  }

  private void executeInterceptorMethod(Consumer<KafkaConsumerInterceptor> consumerInterceptorConsumer, Message<?> message, Object... providedArgs) {
    interceptors.stream()
        .filter(interceptor -> interceptor.filter(message, delegate.getBean(), delegate.getMethod()))
        .forEach(consumerInterceptorConsumer::accept);
  }
}
