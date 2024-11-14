package ru.danilarassokhin.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

/**
 * Table for players.
 */
@Entity(PlayerEntity.TABLE_NAME)
@Setter
@Getter
@Accessors(chain = true)
public class PlayerEntity {

  public static final String TABLE_NAME = "player";

  private Integer id;
  private Integer level;
  private Integer money;
  private Integer experience;
  private String name;

  public PlayerEntity(
      @Column("id") Integer id,
      @Column("level") Integer level,
      @Column("money") Integer money,
      @Column("experience") Integer experience,
      @Column("name") String name
  ) {
    this.id = id;
    this.level = level;
    this.money = money;
    this.experience = experience;
    this.name = name;
  }

  public PlayerEntity addLevel(Integer count) {
    this.setLevel(getLevel() + count);
    return this;
  }

  public PlayerEntity addMoney(Integer count) {
    this.setMoney(getMoney() + count);
    return this;
  }
}
