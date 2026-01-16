package ru.danilarassokhin.notification.command;

import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;

public record SendMailCommand(MailNotificationEntity mailNotification) implements Command<MailNotificationEntity, Void> {

}
