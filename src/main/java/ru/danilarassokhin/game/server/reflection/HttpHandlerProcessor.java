package ru.danilarassokhin.game.server.reflection;

import java.lang.reflect.Method;

import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.util.Pair;

public interface HttpHandlerProcessor {

  Pair<HttpRequestKey, HttpRequestHandler> methodToRequestHandler(Object controller, Method method);

}
