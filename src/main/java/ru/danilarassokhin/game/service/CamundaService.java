package ru.danilarassokhin.game.service;

import java.util.List;
import java.util.UUID;

import ru.danilarassokhin.game.model.ActionDto;

public interface CamundaService {

  void startProcess(UUID id);

  List<ActionDto> getActions(UUID businessKey);

}
