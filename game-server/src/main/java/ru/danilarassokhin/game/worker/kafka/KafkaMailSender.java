package ru.danilarassokhin.game.worker.kafka;

import ru.danilarassokhin.game.entity.MailEntity;

public interface KafkaMailSender {

  void send(MailEntity mailEntity);

}
