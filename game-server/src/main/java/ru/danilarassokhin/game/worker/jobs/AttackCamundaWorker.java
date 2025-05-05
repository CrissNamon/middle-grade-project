package ru.danilarassokhin.game.worker.jobs;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.camunda.CamundaVariables;
import ru.danilarassokhin.game.model.dto.CreateDamageLogDto;
import ru.danilarassokhin.game.service.DungeonService;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.game.worker.CamundaJobWorker;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Attacks dungeon.
 */
@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
@Slf4j
public class AttackCamundaWorker implements CamundaJobWorker {

  private final DungeonService dungeonService;
  private final PlayerService playerService;

  @Override
  public void handle(JobClient client, ActivatedJob job) {
    try {
      var businessKey = job.getVariable(CamundaVariables.BUSINESS_KEY.getCamundaVariableName());
      var playerId = Integer.valueOf(businessKey.toString());
      var player = playerService.getById(playerId);
      var dungeon = dungeonService.findByLevel(player.level());
      dungeonService.attack(new CreateDamageLogDto(playerId, dungeon.id()));
      client.newCompleteCommand(job).send();
    } catch (Throwable t) {
      log.error("Error during attack", t);
      client.newFailCommand(job).retries(0).send();
    }
  }

  @Override
  public String getType() {
    return "attack";
  }
}
