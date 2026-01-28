package ru.danilarassokhin.notification.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.messaging.kafka.KafkaConsumerInterceptor;
import ru.danilarassokhin.messaging.kafka.KafkaConsumerInterceptorMessageHandlerMethodFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig implements KafkaListenerConfigurer {

  private final List<KafkaConsumerInterceptor> decorators;
  private final BeanFactory beanFactory;

  @Bean("retryableKafkaTemplate")
  public KafkaTemplate<String, CreateMailDto> retryableKafkaTemplate(ProducerFactory<String, CreateMailDto> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Override
  public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
    var factory = new KafkaConsumerInterceptorMessageHandlerMethodFactory(decorators);
    factory.setBeanFactory(beanFactory);
    factory.afterPropertiesSet();
    registrar.setMessageHandlerMethodFactory(factory);
  }

}
