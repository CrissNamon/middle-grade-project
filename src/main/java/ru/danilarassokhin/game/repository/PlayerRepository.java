package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.PlayerEntity;

public interface PlayerRepository {

  boolean existsByName(String name);

  void save(PlayerEntity playerEntity);

}
