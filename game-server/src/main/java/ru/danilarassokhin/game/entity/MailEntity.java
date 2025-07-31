package ru.danilarassokhin.game.entity;

import static ru.danilarassokhin.game.entity.MailEntity.TABLE_NAME;

import java.util.UUID;

import ru.danilarassokhin.sql.annotation.Column;
import ru.danilarassokhin.sql.annotation.Entity;

@Entity(TABLE_NAME)
public record MailEntity(
    @Column("id") UUID id,
    @Column("email") String email,
    @Column("text") String text
) {

  public MailEntity(String email, String text) {
    this(null, email, text);
  }

  public static final String TABLE_NAME = "mail";

}
