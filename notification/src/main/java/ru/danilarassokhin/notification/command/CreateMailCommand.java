package ru.danilarassokhin.notification.command;

import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.messaging.dto.CreateMailDto;

public record CreateMailCommand(ru.danilarassokhin.messaging.dto.CreateMailDto dto) implements Command<CreateMailDto> {

}
