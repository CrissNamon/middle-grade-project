package ru.danilarassokhin.game.worker.kafka;

public interface KafkaMailSender {

  void schedule();

  void injectSelf(KafkaMailSender kafkaMailSender);

}
