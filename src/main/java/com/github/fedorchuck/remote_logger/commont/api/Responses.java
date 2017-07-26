package com.github.fedorchuck.remote_logger.commont.api;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
public class Responses {
    public Responses() {
    }

    public static void noContent(RoutingContext routingContext, int code) {
        HttpServerResponse response = routingContext.response().setStatusCode(code);
        nocache(response);
        response.end();
    }

    public static void json(RoutingContext routingContext, int code, Object content) {
        HttpServerResponse response = routingContext.response().setStatusCode(code);
        response.putHeader("content-type", "application/json");
        nocache(response);
        response.end(Json.encode(content));
    }

    public static void plain(RoutingContext routingContext, int code, String text) {
        HttpServerResponse response = routingContext.response().setStatusCode(code);
        response.putHeader("content-type", "text/plain");
        nocache(response);
        response.end(text);
    }

    public static void nocache(HttpServerResponse response) {
        response.putHeader("cache-control", "no-cache").putHeader("expires", "0").putHeader("pragma", "no-cache");
    }
}
