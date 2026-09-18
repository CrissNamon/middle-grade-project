package ru.danilarassokhin.game.worker.kafka;

import ru.danilarassokhin.game.entity.MailEntity;

/**
 * Sends mail to Kafka.
 */
public interface KafkaMailSender {

  /**
   * Sends {@code mailEntity} to mail topic.
   *
   * @param mailEntity Mail to send
   */
  void send(MailEntity mailEntity);

}
