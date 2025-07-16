package ru.danilarassokhin.notification.command;

import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.messaging.model.CreateMailDto;

public record CreateMailCommand(ru.danilarassokhin.messaging.model.CreateMailDto dto) implements Command<CreateMailDto> {

}
