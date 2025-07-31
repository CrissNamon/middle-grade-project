package ru.danilarassokhin.game.worker.camunda.jobs;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.camunda.CamundaVariables;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.game.worker.camunda.CamundaJobWorker;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Retrieves dungeon level from user and sets variable in process.
 */
@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class GetDungeonWorker implements CamundaJobWorker {

  private final PlayerService playerService;

  @Override
  public String getType() {
    return "getDungeon";
  }

  @Override
  public void handle(JobClient client, ActivatedJob job) {
    var businessKey = job.getVariable(CamundaVariables.BUSINESS_KEY.getCamundaVariableName());
    var playerId = Integer.valueOf(businessKey.toString());
    var player = playerService.getById(playerId);
    client.newCompleteCommand(job)
        .variable(CamundaVariables.LEVEL.getCamundaVariableName(), player.level())
        .send();
  }
}
