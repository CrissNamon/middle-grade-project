package ru.danilarassokhin.game.mapper;

import java.util.List;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import ru.danilarassokhin.game.entity.camunda.CamundaActionEntity;
import ru.danilarassokhin.game.entity.camunda.CamundaProcessEntity;
import ru.danilarassokhin.game.model.dto.CamundaActionDto;
import ru.danilarassokhin.game.model.request.CamundaActionRequest;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Mapper for Camunda objects.
 */
@Mapper
@AnnotateWith(GameBean.class)
public interface CamundaMapper {

  /**
   * Converts {@link ProcessInstanceEvent} to {@link CamundaProcessEntity}.
   * @param event {@link ProcessInstanceEvent}
   * @return {@link CamundaProcessEntity}
   */
  default CamundaProcessEntity processInstanceEventToEntity(ProcessInstanceEvent event) {
    return new CamundaProcessEntity(event.getProcessInstanceKey());
  }

  /**
   * Converts {@link CamundaActionEntity} to {@link CamundaActionDto}.
   * @param entity {@link CamundaActionEntity}
   * @return {@link CamundaActionDto}
   */
  CamundaActionDto camundaActionEntityToDto(CamundaActionEntity entity);

  /**
   * Converts list of {@link CamundaActionEntity} to list of{@link CamundaActionDto}.
   * @param entities {@link CamundaActionEntity}
   * @return {@link CamundaActionDto}
   */
  List<CamundaActionDto> camundaActionEntitiesToDtos(List<CamundaActionEntity> entities);

  /**
   * Converts {@link CamundaActionDto} to {@link CamundaActionEntity}.
   * @param camundaActionDto {@link CamundaActionDto}
   * @return {@link CamundaActionEntity}
   */
  CamundaActionEntity camundaActionDtoToEntity(CamundaActionDto camundaActionDto);

  /**
   * Converts {@link CamundaActionRequest} to {@link CamundaActionDto}.
   * @param camundaActionRequest {@link CamundaActionRequest}
   * @return {@link CamundaActionDto}
   */
  CamundaActionDto camundaActionRequestToDto(CamundaActionRequest camundaActionRequest);

}
