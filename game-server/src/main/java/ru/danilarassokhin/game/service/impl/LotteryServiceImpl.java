package ru.danilarassokhin.game.service.impl;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.repository.LockRepository;
import ru.danilarassokhin.game.repository.LotteryRepository;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.LotteryService;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.sql.annotation.Transactional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class LotteryServiceImpl implements LotteryService {

  private static final String LOCK_NAME = "MoneyService#attendLottery";
  private static final Integer LOCK_TIME_OUT = 2;
  private static final Integer LOTTERY_PRIZE = 100;
  private static final Long MAX_PRIZES = 100L;

  private final LockRepository lockRepository;
  private final PlayerRepository playerRepository;
  private final LotteryRepository lotteryRepository;

  @Override
  @Transactional
  public void attendLottery(Integer playerId) {
    lockRepository.acquireLockOrWait(LOCK_NAME, LOCK_TIME_OUT);
    var player = playerRepository.findById(playerId)
        .orElseThrow(() -> new ApplicationException("Player not found"));
    if (lotteryRepository.countPlayers() >= MAX_PRIZES) {
      throw new ApplicationException("Lottery is finished");
    }
    if (!lotteryRepository.existsByPlayer(player)) {
      playerRepository.update(player.addMoney(LOTTERY_PRIZE));
      lotteryRepository.addPlayer(player);
      return;
    }
    throw new ApplicationException("Player already grabbed the prize");
  }
}
