package ru.danilarassokhin.statistic.service;

import java.util.List;

import ru.danilarassokhin.statistic.dto.GameEventDto;
import ru.danilarassokhin.statistic.dto.GameEventFilter;

public interface GameEventService {

  List<GameEventDto> findByFilters(GameEventFilter filter);

}
