package ru.danilarassokhin.game.service;

import java.util.List;

import ru.danilarassokhin.game.model.dto.CamundaActionDto;

public interface CamundaService {

  List<CamundaActionDto> getAvailableActions(Integer businessKey);

}
