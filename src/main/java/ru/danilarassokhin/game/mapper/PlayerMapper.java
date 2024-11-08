package ru.danilarassokhin.game.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;
import ru.danilarassokhin.game.model.request.CreatePlayerRequest;
import tech.hiddenproject.progressive.annotation.GameBean;

@Mapper
@AnnotateWith(GameBean.class)
public interface PlayerMapper {

  CreatePlayerDto createPlayerRequestToDto(CreatePlayerRequest createPlayerRequest);

  PlayerDto playerEntityToDto(PlayerEntity playerEntity);

  PlayerEntity createPlayerDtoToEntity(CreatePlayerDto createPlayerDto);

}
