package ru.danilarassokhin.game.reflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.danilarassokhin.game.model.TestController;
import ru.danilarassokhin.game.server.model.HttpMediaType;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;
import ru.danilarassokhin.game.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.game.server.reflection.impl.HttpHandlerProcessorImpl;

public class HttpHandlerProcessorImplTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpBodyMapper httpBodyMapper = new HttpBodyMapperImpl(objectMapper);
  private final HttpHandlerProcessorImpl httpHandlerProcessor = new HttpHandlerProcessorImpl(httpBodyMapper);
  private final TestController testController = new TestController();

  @Test
  @DisplayName("it should create request key from controller method")
  public void itShouldCreateRequestHandlerAndKeyFromControllerMethod() {
    Assertions.assertDoesNotThrow(() -> {
      var method = testController.getClass().getDeclaredMethod("ping", FullHttpRequest.class);
      var expectedKey = new HttpRequestKey(HttpMethod.GET, HttpMediaType.APPLICATION_JSON, "/ping");

      var actual = httpHandlerProcessor.methodToRequestHandler(testController, method);
      Assertions.assertEquals(expectedKey, actual.first());
    });
  }

  @Test
  @DisplayName("it should create request handler from controller method and execute successfully")
  public void itShouldCreateAndExecuteRequestHandlerFromControllerMethod() {
    Assertions.assertDoesNotThrow(() -> {
      var method = testController.getClass().getDeclaredMethod("ping", FullHttpRequest.class);
      var requestMock = Mockito.mock(FullHttpRequest.class);

      var actual = httpHandlerProcessor.methodToRequestHandler(testController, method);
      var response = actual.second().handle(requestMock);
      Assertions.assertEquals("\"pong\"", response.body());
    });
  }
}
