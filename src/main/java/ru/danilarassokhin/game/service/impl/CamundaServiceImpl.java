package ru.danilarassokhin.game.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.model.dto.CamundaActionDto;
import ru.danilarassokhin.game.service.CamundaService;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor
public class CamundaServiceImpl implements CamundaService {

  @Override
  public List<CamundaActionDto> getAvailableActions(Integer businessKey) {
    return List.of();
  }
}
