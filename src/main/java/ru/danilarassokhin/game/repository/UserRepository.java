package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.UserEntity;
import ru.danilarassokhin.game.service.annotation.GameRepository;
import ru.danilarassokhin.game.service.annotation.Select;
import ru.danilarassokhin.game.service.annotation.Update;

@GameRepository(UserEntity.class)
public interface UserRepository {

  @Update("""
    INSERT INTO :table:(name) VALUES(?) ON CONFLICT DO NOTHING;
    """)
  int save(String name);

  @Select("SELECT * FROM :table:;")
  List<UserEntity> findAll();

}
