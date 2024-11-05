package ru.danilarassokhin.game.reflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.danilarassokhin.game.model.TestController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.game.server.model.HttpMediaType;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;
import ru.danilarassokhin.game.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.game.server.reflection.impl.HttpHandlerProcessorImpl;

public class ReflectiveDispatcherControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpBodyMapper httpBodyMapper = new HttpBodyMapperImpl(objectMapper);
  private final HttpHandlerProcessorImpl handlerProcessor = new HttpHandlerProcessorImpl(httpBodyMapper);
  private final ReflectiveDispatcherController dispatcherController = new ReflectiveDispatcherController(handlerProcessor);
  private final HttpHandlerProcessorImpl httpHandlerProcessor = new HttpHandlerProcessorImpl(httpBodyMapper);
  private final TestController testController = new TestController();

  @Test
  @DisplayName("it should find existing handler by key successfully")
  public void itShouldFindExistingHandlerByKeySuccessfully() {
    var key = new HttpRequestKey(HttpMethod.GET, HttpMediaType.APPLICATION_JSON, "/uri");
    dispatcherController.addMapping(key, httpRequest -> new HttpResponseEntity(HttpMediaType.APPLICATION_JSON, "", HttpResponseStatus.OK));

    var actual = dispatcherController.findByKey(key);
    Assertions.assertTrue(actual.isPresent());
  }

  @Test
  @DisplayName("it should find not existing handler by key successfully and return empty optional")
  public void itShouldFindNotExistingHandlerByKeyAndReturnEmptyOptional() {
    var key = new HttpRequestKey(HttpMethod.GET, HttpMediaType.APPLICATION_JSON, "/wrong");

    var actual = dispatcherController.findByKey(key);
    Assertions.assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("it should handle request successfully")
  public void itShouldHandleRequestSuccessfully() {
    Assertions.assertDoesNotThrow(() -> {
      var controllerMethod = testController.getClass().getDeclaredMethod("ping", FullHttpRequest.class);
      var handler = httpHandlerProcessor.methodToRequestHandler(testController, controllerMethod);
      var mockRequest = Mockito.mock(FullHttpRequest.class);
      var mockHeaders = Mockito.mock(HttpHeaders.class);
      Mockito.when(mockRequest.headers()).thenReturn(mockHeaders);
      Mockito.when(mockRequest.method()).thenReturn(HttpMethod.GET);
      Mockito.when(mockRequest.uri()).thenReturn("/ping");
      Mockito.when(mockHeaders.get(HttpHeaderNames.CONTENT_TYPE)).thenReturn(HttpMediaType.APPLICATION_JSON);
      dispatcherController.addMapping(handler.getLeft(), handler.getRight());

      var actual = dispatcherController.handleRequest(mockRequest);
      Assertions.assertEquals("\"pong\"", actual.body());
    });
  }

  @Test
  @DisplayName("it should handle request and return method not allowed")
  public void itShouldHandleRequestAndReturnMethodNotAllowed() {
    Assertions.assertDoesNotThrow(() -> {
      var mockRequest = Mockito.mock(FullHttpRequest.class);
      var mockHeaders = Mockito.mock(HttpHeaders.class);
      Mockito.when(mockRequest.headers()).thenReturn(mockHeaders);
      Mockito.when(mockRequest.method()).thenReturn(HttpMethod.GET);
      Mockito.when(mockRequest.uri()).thenReturn("/wrong");
      Mockito.when(mockHeaders.get(HttpHeaderNames.CONTENT_TYPE)).thenReturn(HttpMediaType.TEXT_PLAIN);

      var actual = dispatcherController.handleRequest(mockRequest);
      Assertions.assertEquals(HttpResponseStatus.METHOD_NOT_ALLOWED, actual.status());
    });
  }
}
