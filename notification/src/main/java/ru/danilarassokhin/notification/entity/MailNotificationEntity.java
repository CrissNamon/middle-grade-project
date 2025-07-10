package ru.danilarassokhin.notification.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "mail")
@Accessors(chain = true)
@Getter
@Setter
@ToString
public class MailNotificationEntity {

  @Id
  private UUID id;
  private String email;
  @Column(value = "date_time")
  private LocalDateTime dateTime;
  @Column(value = "is_processed")
  private boolean isProcessed;

  public MailNotificationEntity(String email) {
    this.email = email;
  }
}
