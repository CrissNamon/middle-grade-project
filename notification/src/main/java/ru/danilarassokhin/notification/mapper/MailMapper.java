package ru.danilarassokhin.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.messaging.model.CreateMailDto;

@Mapper(componentModel = "spring")
public interface MailMapper {

  @Mapping(target = "processed", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "dateTime", ignore = true)
  MailNotificationEntity createMailDtoToEntity(CreateMailDto dto);

}
