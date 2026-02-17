package ru.danilarassokhin.statistic.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import ru.danilarassokhin.messaging.dto.event.EventDto;
import ru.danilarassokhin.statistic.annotation.EmbeddedKafkaTest;
import ru.danilarassokhin.statistic.service.impl.GlobalFeedService;
import ru.danilarassokhin.statistic.util.PojoCreator;

@SpringBootTest
@EmbeddedKafkaTest
public class GlobalFeedServiceTest {

  @Autowired
  GlobalFeedService globalFeedService;

  @Autowired
  KafkaTemplate<String, EventDto> kafkaTemplate;

  @Value("${app.events.topic}")
  String topic;

  @Test
  void itShouldProduceMessagesToTopicAndReceiveFeedSuccessfully() {
    var events = PojoCreator.createEventsAndFeedMessages();
    events.forEach(event -> kafkaTemplate.send(topic, event.left).join());
    kafkaTemplate.flush();
    var result = globalFeedService.getAll().take(events.size()).collectList().block();
    Assertions.assertEquals(events.size(), result.size());
    events.forEach(event -> Assertions.assertTrue(
        result.stream().anyMatch(feedDto -> feedDto.text().matches(event.right))));
  }

}
