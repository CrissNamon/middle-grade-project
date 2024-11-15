package ru.danilarassokhin.game.mapper;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import ru.danilarassokhin.game.entity.CamundaProcessEntity;
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

}
