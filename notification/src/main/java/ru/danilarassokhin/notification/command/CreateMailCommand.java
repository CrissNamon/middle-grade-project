package ru.danilarassokhin.notification.command;

import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.messaging.dto.CreateMailDto;

public record CreateMailCommand(CreateMailDto dto) implements Command<CreateMailDto> {

}
