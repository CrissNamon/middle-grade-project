package ru.danilarassokhin.game.server.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Unique identity of {@link ru.danilarassokhin.game.server.HttpRequestHandler}.
 *
 * @param method {@link HttpMethod}
 * @param contentType Content type handler consumes
 * @param uri Url handler will listen
 */
public record HttpRequestKey(HttpMethod method, String contentType, String uri, Pattern uriPattern) {

  private static final String URL_PATH_DELIMITER = "/";
  private static final String PATH_PARAMETER_PREFIX = "{";
  private static final String PATH_PARAMETER_TEMPLATE_MIDDLE = "\\{.*}/";
  private static final String PATH_PARAMETER_PATTERN_MIDDLE = "(.*)/";
  private static final String PATH_PARAMETER_TEMPLATE_LAST = "(\\{.*})";
  private static final String PATH_PARAMETER_PATTERN_LAST = "([^/]+)(?!/)";

  public HttpRequestKey(HttpMethod method, String contentType, String uri) {
    this(method, contentType, uri, createUriPattern(uri));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    HttpRequestKey that = (HttpRequestKey) object;
    return Objects.equals(uri, that.uri) && Objects.equals(method, that.method)
        && Objects.equals(contentType, that.contentType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, contentType, uri);
  }

  public boolean matches(HttpRequestKey other) {
    return this.contentType.equals(other.contentType()) &&
        this.method.equals(other.method()) &&
        this.uriPattern.matcher(other.uri()).matches();
  }

  public Map<String, Object> extractParametersNamed(String requestUri) {
    var patternPaths = uri.split(URL_PATH_DELIMITER);
    var uriPaths = requestUri.split(URL_PATH_DELIMITER);
    return IntStream.range(0, patternPaths.length)
        .mapToObj(index -> {
          if (patternPaths[index].startsWith(PATH_PARAMETER_PREFIX)) {
            var name = patternPaths[index].substring(1, patternPaths[index].length() - 1);
            var value = castParameterValue(uriPaths[index]);
            return ImmutablePair.of(name, value);
          }
          return null;
        })
        .filter(Objects::nonNull)
        .collect(HashMap::new, (map, pair) -> map.put(pair.getLeft(), pair.getRight()), HashMap::putAll);
  }

  private Object castParameterValue(String value) {
    return Stream.<Function<String, Object>>of(this::tryCastInteger, this::tryCastLong)
        .filter(stringObjectFunction -> Objects.nonNull(stringObjectFunction.apply(value)))
        .findFirst()
        .map(stringObjectFunction -> stringObjectFunction.apply(value))
        .orElse(value);
  }

  private Integer tryCastInteger(String value) {
    try {
      return Integer.parseInt(value);
    } catch (Throwable t) {
      return null;
    }
  }

  private Long tryCastLong(String value) {
    try {
      return Long.parseLong(value);
    } catch (Throwable t) {
      return null;
    }
  }

  private static String trim(String uri) {
    if (uri.endsWith(URL_PATH_DELIMITER)) {
      return uri.substring(0, uri.length() - 1);
    }
    return uri;
  }

  private static Pattern createUriPattern(String uri) {
    var regex = trim(uri).replaceAll(PATH_PARAMETER_TEMPLATE_MIDDLE, PATH_PARAMETER_PATTERN_MIDDLE)
        .replaceFirst(PATH_PARAMETER_TEMPLATE_LAST, PATH_PARAMETER_PATTERN_LAST);
    return Pattern.compile(regex);
  }
}
