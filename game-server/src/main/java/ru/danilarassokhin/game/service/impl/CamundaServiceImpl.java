package ru.danilarassokhin.game.service.impl;

import static ru.danilarassokhin.game.config.CamundaConfig.CAMUNDA_DEPLOYMENTS_PROPERTY;
import static ru.danilarassokhin.game.config.CamundaConfig.CAMUNDA_PROCESS_ID_PROPERTY;

import java.util.List;
import java.util.Map;

import ru.danilarassokhin.game.entity.camunda.CamundaVariables;
import ru.danilarassokhin.game.exception.CamundaException;
import ru.danilarassokhin.game.mapper.CamundaMapper;
import ru.danilarassokhin.game.model.dto.CamundaActionDto;
import ru.danilarassokhin.game.repository.CamundaRepository;
import ru.danilarassokhin.game.service.CamundaService;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class CamundaServiceImpl implements CamundaService {

  private final String processId;
  private final PropertiesFactory propertiesFactory;
  private final CamundaRepository camundaRepository;
  private final CamundaMapper camundaMapper;

  @Autofill
  public CamundaServiceImpl(
      PropertiesFactory propertiesFactory,
      CamundaRepository camundaRepository,
      CamundaMapper camundaMapper
  ) {
    this.propertiesFactory = propertiesFactory;
    this.processId = propertiesFactory.getAsString(CAMUNDA_PROCESS_ID_PROPERTY).orElseThrow();
    this.camundaRepository = camundaRepository;
    this.camundaMapper = camundaMapper;
    deployAllProcesses();
  }

  @Override
  public void createProcess(Integer businessKey) {
    var variables = Map.<String, Object>of(
        CamundaVariables.LEVEL.getCamundaVariableName(), 1,
        CamundaVariables.BUSINESS_KEY.getCamundaVariableName(), businessKey
    );
    ThrowableOptional.sneaky(() -> camundaRepository.createProcess(processId, variables), CamundaException::new);
  }

  @Override
  public List<CamundaActionDto> getAvailableActions(Integer businessKey) {
    return camundaMapper.camundaActionEntitiesToDtos(camundaRepository.getAvailableActions(businessKey));
  }

  @Override
  public void doAction(CamundaActionDto camundaActionDto) {
    camundaRepository.doAction(camundaMapper.camundaActionDtoToEntity(camundaActionDto));
  }

  private void deployAllProcesses() {
    propertiesFactory.getAsString(CAMUNDA_DEPLOYMENTS_PROPERTY).ifPresent(camundaRepository::deployProcess);
  }
}
