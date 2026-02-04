package ru.danilarassokhin.game.kafka;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.danilarassokhin.game.service.ClientAuthenticationService;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.messaging.dto.KafkaHeader;
import ru.danilarassokhin.messaging.kafka.producer.ConfigurableKafkaProducerInterceptor;

@Slf4j
public class KafkaAuthorizationProducerInterceptor extends ConfigurableKafkaProducerInterceptor<String, CreateMailDto> {
  private final ClientAuthenticationService clientAuthenticationService;

  public KafkaAuthorizationProducerInterceptor(
      Predicate<ProducerRecord<String, CreateMailDto>> filter,
      ClientAuthenticationService clientAuthenticationService) {
    super(filter);
    this.clientAuthenticationService = clientAuthenticationService;
  }

  @Override
  public ProducerRecord<String, CreateMailDto> beforeSend(ProducerRecord<String, CreateMailDto> record) {
    log.info("Before send: {}", record);
    record.headers().add(KafkaHeader.AUTHENTICATION, clientAuthenticationService.getToken().getBytes(StandardCharsets.UTF_8));
    return record;
  }
}
