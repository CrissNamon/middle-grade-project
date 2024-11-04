package ru.danilarassokhin.game;

import java.util.UUID;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.CamundaService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class TestController {

  private final CamundaService camundaService;

  @GetRequest("/start")
  public ResponseEntity start(FullHttpRequest request) {
    var id = UUID.randomUUID();
    camundaService.startProcess(id);
    return ResponseEntity.ok(id);
  }

  @GetRequest("/actions")
  public ResponseEntity actions(ActionRequest request) {
    var actions = camundaService.getActions(request.id);
    return ResponseEntity.ok(actions);
  }

  public record ActionRequest(UUID id) { }

}
