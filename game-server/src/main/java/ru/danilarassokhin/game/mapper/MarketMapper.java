package ru.danilarassokhin.game.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.danilarassokhin.game.entity.MarketEntity;
import ru.danilarassokhin.game.model.dto.CreateMarketItemDto;
import ru.danilarassokhin.game.model.dto.MarketItemDto;
import ru.danilarassokhin.game.model.request.CreateMarketItemRequest;
import tech.hiddenproject.progressive.annotation.GameBean;

@Mapper
@AnnotateWith(GameBean.class)
public interface MarketMapper {

  /**
   * Converts {@link CreateMarketItemDto} to {@link MarketEntity}.
   * @param createMarketItemDto {@link CreateMarketItemDto}
   * @return {@link MarketEntity}
   */
  @Mapping(source = "item", target = "itemCode")
  MarketEntity createMarketEntityFromCreateMarketItemDto(CreateMarketItemDto createMarketItemDto);

  /**
   * Converts {@link MarketEntity} to {@link MarketItemDto}.
   * @param marketEntity {@link MarketEntity}
   * @return {@link MarketItemDto}
   */
  @Mapping(source = "itemCode", target = "item")
  MarketItemDto marketEntityToMarketItemDto(MarketEntity marketEntity);

  /**
   * Converts {@link CreateMarketItemRequest} to {@link CreateMarketItemDto}.
   * @param createMarketItemRequest {@link CreateMarketItemRequest}
   * @return {@link CreateMarketItemDto}
   */
  CreateMarketItemDto createMarketItemRequestToDto(CreateMarketItemRequest createMarketItemRequest);

}
