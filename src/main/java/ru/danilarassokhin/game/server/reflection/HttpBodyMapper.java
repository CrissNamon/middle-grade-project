package ru.danilarassokhin.game.server.reflection;

public interface HttpBodyMapper {

  String objectToString(String contentType, Object body);

  Object stringToObject(String contentType, String body, Class<?> bodyType);

}
