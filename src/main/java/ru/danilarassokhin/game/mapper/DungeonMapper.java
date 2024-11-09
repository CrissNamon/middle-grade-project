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

  DungeonDto dungeonEntityToDto(DungeonEntity entity);

  DungeonEntity createDungeonDtoToEntity(CreateDungeonDto createDungeonDto);

  CreateDungeonDto createDungeonRequestToDto(CreateDungeonRequest request);

  CreateDamageLogDto createDungeonLogDtoFromRequest(DungeonAttackRequest request);

}
