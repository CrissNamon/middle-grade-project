package ru.danilarassokhin.game.service.impl;

import static ru.danilarassokhin.game.config.DataSourceConfig.TRANSACTION_DEFAULT_RETRY_COUNT;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.PlayerItem;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.mapper.MarketMapper;
import ru.danilarassokhin.game.model.dto.CreateMarketItemDto;
import ru.danilarassokhin.game.model.dto.MarketItemDto;
import ru.danilarassokhin.game.repository.MarketRepository;
import ru.danilarassokhin.game.repository.PlayerItemRepository;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.MarketService;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import ru.danilarassokhin.game.util.AwaitUtil;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
@Slf4j
public class MarketServiceImpl implements MarketService {

  private final TransactionManager transactionManager;
  private final MarketMapper marketMapper;
  private final MarketRepository marketRepository;
  private final PlayerItemRepository playerItemRepository;
  private final PlayerRepository playerRepository;

  @Override
  public MarketItemDto create(CreateMarketItemDto createMarketItemDto) {
    return transactionManager.fetchInTransaction(ctx -> {
      var id = marketRepository.save(ctx, marketMapper.createMarketEntityFromCreateMarketItemDto(createMarketItemDto));
      return marketRepository.findById(ctx, id)
          .map(marketMapper::marketEntityToMarketItemDto)
          .orElseThrow(() -> new ApplicationException("Error creating market item"));
    });
  }

  //Используем REPEATABLE READ т.к. несколько транзакций изменяют одну строку.
  //Т.к. REPEATABLE READ в Postgres использует что-то похожее на оптимистичную блокировку,
  //то транзакция может упасть с ошибкой конкурентного чтения/записи
  //Повторяем транзакцию пока не выполнится, но не более 10 раз
  @Override
  public void buy(Integer playerId, Integer itemId) {
    AwaitUtil.retryOnError(TRANSACTION_DEFAULT_RETRY_COUNT, () -> buyItemTransaction(playerId, itemId),
                           () -> log.warn("Transaction buyItem(Integer, Integer) failed. Retrying.."),
                           DataSourceException.class, SQLException.class);
  }

  private void buyItemTransaction(Integer playerId, Integer itemId) {
    transactionManager.doInTransaction(Connection.TRANSACTION_REPEATABLE_READ, ctx -> {
      var item = marketRepository.findById(ctx, itemId)
          .orElseThrow(() -> new ApplicationException("Item not found"));
      var player = playerRepository.findById(ctx, playerId)
          .orElseThrow(() -> new ApplicationException("Player not found"));
      if (item.amount() <= 0) {
        throw new ApplicationException("Not enough items");
      }
      if (player.getMoney() >= item.price()) {
        playerItemRepository.save(ctx, new PlayerItem(null, playerId, item.itemCode(), 1));
        marketRepository.decreaseItem(ctx, itemId);
        return;
      }
      throw new ApplicationException("Not enough money");
    });
  }
}
