package ru.danilarassokhin.game.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import tech.hiddenproject.progressive.annotation.GameBean;

@Mapper
@AnnotateWith(GameBean.class)
public interface MailMapper {

  CreateMailDto mailEntityToCreateMailDto(MailEntity mailEntity);

}
