package ru.danilarassokhin.game.controller;

import jakarta.validation.ConstraintViolationException;
import ru.danilarassokhin.game.model.HttpResponse;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.impl.HttpExceptionHandler;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class GlobalExceptionHandler {

  @Autofill
  public GlobalExceptionHandler(HttpExceptionHandler handler) {
    handler.addHandler(ConstraintViolationException.class,
                       c -> ResponseEntity.badRequest(new HttpResponse(c.getMessage())));
  }
}