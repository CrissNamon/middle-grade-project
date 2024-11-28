package ru.danilarassokhin.game.repository;

import java.util.Optional;

import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface CatalogueDungeonRepository {

  Optional<CatalogueDungeonEntity> findByCode(TransactionContext ctx, String code);

}
