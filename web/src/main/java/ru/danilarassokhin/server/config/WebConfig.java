package ru.danilarassokhin.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validator;
import ru.danilarassokhin.server.HttpExceptionHandler;
import ru.danilarassokhin.server.reflection.HttpBodyMapper;
import ru.danilarassokhin.server.reflection.HttpHandlerProcessor;
import ru.danilarassokhin.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.server.reflection.impl.HttpHandlerProcessorImpl;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class WebConfig {

  public static final String WEB_SERVER_PORT_PROPERTY_NAME = "server.port";
  public static final String WEB_SERVER_SHUTDOWN_EVENT_NAME = "server-shutdown";

  @GameBean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  @GameBean(order = 1)
  public HttpBodyMapper httpBodyMapper(ObjectMapper objectMapper) {
    return new HttpBodyMapperImpl(objectMapper);
  }

  @GameBean(order = 2)
  public HttpHandlerProcessor httpHandlerProcessor(
      HttpBodyMapper httpBodyMapper,
      Validator validator,
      HttpExceptionHandler httpExceptionHandler
  ) {
    return new HttpHandlerProcessorImpl(httpBodyMapper, validator, httpExceptionHandler);
  }

}
