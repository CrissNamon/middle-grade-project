package ru.danilarassokhin.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface HttpResponseInterceptor {

  void intercept(FullHttpRequest request, HttpResponse response);

}
