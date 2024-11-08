package ru.danilarassokhin.game.controller;

import jakarta.validation.ConstraintViolationException;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.model.response.HttpErrorResponse;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.impl.HttpExceptionHandler;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class GlobalExceptionHandler {

  @Autofill
  public GlobalExceptionHandler(HttpExceptionHandler handler) {
    handler.addHandler(ConstraintViolationException.class,
                       c -> ResponseEntity.badRequest(new HttpErrorResponse(c.getMessage())));
    handler.addHandler(ApplicationException.class,
                       c -> ResponseEntity.badRequest(new HttpErrorResponse(c.getMessage())));
  }

}
