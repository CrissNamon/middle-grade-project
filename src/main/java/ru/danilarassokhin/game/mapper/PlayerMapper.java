package ru.danilarassokhin.game.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.model.CreatePlayerDto;
import tech.hiddenproject.progressive.annotation.GameBean;

@Mapper
@AnnotateWith(GameBean.class)
public interface PlayerMapper {

  PlayerEntity createPlayerDtoToPlayerEntity(CreatePlayerDto createPlayerDto);

}
