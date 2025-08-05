package ru.danilarassokhin.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.danilarassokhin.cqrs.query.QueryMediator;
import ru.danilarassokhin.jaxb.GetAllMailRequest;
import ru.danilarassokhin.jaxb.GetAllMailResponse;
import ru.danilarassokhin.jaxb.Mail;
import ru.danilarassokhin.notification.annotation.FeatureToggle;
import ru.danilarassokhin.notification.mapper.MailMapper;
import ru.danilarassokhin.notification.query.FindAllMailsQuery;

@Endpoint
@RequiredArgsConstructor
public class MailController {

  private final QueryMediator queryMediator;
  private final MailMapper mapper;

  @PayloadRoot(namespace = "http://mail-service.com/soap-web-service", localPart = "getAllMailRequest")
  @ResponsePayload
  @FeatureToggle("${soap.endpoints.get-all-mails}")
  public GetAllMailResponse getAllMails(@RequestPayload GetAllMailRequest request) {
    return queryMediator.execute(new FindAllMailsQuery())
        .map(mapper::createMailFromEntity)
        .reduce(new GetAllMailResponse(), this::addMail)
        .block();
  }

  private GetAllMailResponse addMail(GetAllMailResponse response, Mail mail) {
    response.getMail().add(mail);
    return response;
  }

}
