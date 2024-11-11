package ru.danilarassokhin.game.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.model.dto.CreateDamageLogDto;
import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;
import ru.danilarassokhin.game.model.request.CreateDungeonRequest;
import ru.danilarassokhin.game.model.request.DungeonAttackRequest;
import tech.hiddenproject.progressive.annotation.GameBean;

@Mapper
@AnnotateWith(GameBean.class)
public interface DungeonMapper {

  /**
   * Converts {@link DungeonEntity} to {@link DungeonDto}.
   * @param entity {@link DungeonEntity}
   * @return {@link DungeonDto}
   */
  DungeonDto dungeonEntityToDto(DungeonEntity entity);

  /**
   * Converts {@link CreateDungeonDto} to {@link DungeonEntity}.
   * @param createDungeonDto {@link CreateDungeonDto}
   * @return {@link DungeonEntity}
   */
  DungeonEntity createDungeonDtoToEntity(CreateDungeonDto createDungeonDto);

  /**
   * Converts {@link CreateDungeonRequest} to {@link CreateDungeonDto}.
   * @param request {@link CreateDungeonRequest}
   * @return {@link CreateDungeonDto}
   */
  CreateDungeonDto createDungeonRequestToDto(CreateDungeonRequest request);

  /**
   * Converts {@link DungeonAttackRequest} to {@link CreateDamageLogDto}.
   * @param request {@link DungeonAttackRequest}
   * @return {@link CreateDamageLogDto}
   */
  CreateDamageLogDto createDungeonLogDtoFromRequest(DungeonAttackRequest request);

}
