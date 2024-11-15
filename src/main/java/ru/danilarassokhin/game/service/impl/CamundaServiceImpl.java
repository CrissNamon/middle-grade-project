package ru.danilarassokhin.game.service.impl;

import java.util.List;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.CamundaActionEntity;
import ru.danilarassokhin.game.repository.CamundaRepository;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class CamundaServiceImpl {

  private final ZeebeClient zeebeClient;
  private final CamundaRepository camundaRepository;

  public List<CamundaActionEntity> getActions(Integer businessKey) {
    return camundaRepository.getAvailableActions(businessKey);
  }

  public void doAction(CamundaActionEntity camundaActionEntity) {
    camundaRepository.doAction(camundaActionEntity);
  }

}
