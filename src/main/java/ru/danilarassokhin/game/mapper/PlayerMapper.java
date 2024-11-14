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

  /**
   * Converts {@link CreatePlayerRequest} to {@link CreatePlayerDto}.
   * @param createPlayerRequest {@link CreatePlayerRequest}
   * @return {@link CreatePlayerDto}
   */
  CreatePlayerDto createPlayerRequestToDto(CreatePlayerRequest createPlayerRequest);

  /**
   * Converts {@link PlayerEntity} to {@link PlayerDto}.
   * @param playerEntity {@link PlayerEntity}
   * @return {@link PlayerDto}
   */
  PlayerDto playerEntityToDto(PlayerEntity playerEntity);

  /**
   * Converts {@link CreatePlayerDto} to {@link PlayerEntity}.
   * @param createPlayerDto {@link CreatePlayerDto}
   * @return {@link PlayerEntity}
   */
  PlayerEntity createPlayerDtoToEntity(CreatePlayerDto createPlayerDto);

}
