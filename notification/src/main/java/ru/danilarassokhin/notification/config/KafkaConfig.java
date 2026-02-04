package ru.danilarassokhin.notification.config;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import ru.danilarassokhin.messaging.kafka.consumer.KafkaConsumerInterceptorFilter;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.messaging.kafka.consumer.KafkaConsumerInterceptor;
import ru.danilarassokhin.messaging.kafka.consumer.KafkaConsumerInterceptorMessageHandlerMethodFactory;
import ru.danilarassokhin.notification.annotation.ReactiveAuthorized;
import ru.danilarassokhin.notification.message.KafkaConsumerJwtInterceptor;

@Configuration
public class KafkaConfig implements KafkaListenerConfigurer {

  @Autowired
  @Lazy
  private List<KafkaConsumerInterceptor> decorators;

  @Autowired
  @Lazy
  private BeanFactory beanFactory;

  @Bean("retryableKafkaTemplate")
  public KafkaTemplate<String, CreateMailDto> retryableKafkaTemplate(ProducerFactory<String, CreateMailDto> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public KafkaConsumerJwtInterceptor kafkaConsumerJwtInterceptor(
      SpelExpressionParser spelExpressionParser,
      ParameterNameDiscoverer parameterNameDiscoverer,
      ReactiveJwtDecoder reactiveJwtDecoder
  ) {
    return new KafkaConsumerJwtInterceptor(
        KafkaConsumerInterceptorFilter.filterByTopicRegex(".*")
            .and(KafkaConsumerInterceptorFilter.filterByAnnotation(ReactiveAuthorized.class)),
        spelExpressionParser,
        parameterNameDiscoverer,
        reactiveJwtDecoder
    );
  }

  @Override
  public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
    var factory = new KafkaConsumerInterceptorMessageHandlerMethodFactory(decorators);
    factory.setBeanFactory(beanFactory);
    factory.afterPropertiesSet();
    registrar.setMessageHandlerMethodFactory(factory);
  }

}
