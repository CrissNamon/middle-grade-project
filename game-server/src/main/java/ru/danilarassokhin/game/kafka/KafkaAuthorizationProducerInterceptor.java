package ru.danilarassokhin.game.kafka;

import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.danilarassokhin.game.service.ClientAuthenticationService;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.messaging.dto.KafkaHeader;
import ru.danilarassokhin.messaging.kafka.KafkaProducerInterceptor;

@RequiredArgsConstructor
@Slf4j
public class KafkaAuthorizationProducerInterceptor implements KafkaProducerInterceptor<String, CreateMailDto> {

  private final ClientAuthenticationService clientAuthenticationService;

  @Override
  public boolean filter(ProducerRecord<String, CreateMailDto> record) {
    return true;
  }

  @Override
  public ProducerRecord<String, CreateMailDto> beforeSend(ProducerRecord<String, CreateMailDto> record) {
    log.info("Before send: {}", record);
    record.headers().add(KafkaHeader.AUTHENTICATION, clientAuthenticationService.getToken().getBytes(StandardCharsets.UTF_8));
    return record;
  }
}
