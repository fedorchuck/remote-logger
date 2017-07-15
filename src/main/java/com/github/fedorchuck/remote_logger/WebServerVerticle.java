package com.github.fedorchuck.remote_logger;


import com.github.fedorchuck.remote_logger.controllers.LoggerController;
import com.github.fedorchuck.remote_logger.util.NullCheckUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;


public class WebServerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // setup HTTP server
        Router router = Router.router(vertx);
        router.route().handler(CookieHandler.create());

        router.mountSubRouter("/log", new LoggerController(vertx).getRouter());

        int port = NullCheckUtil.tryParseInteger(System.getenv("SERVER_PORT"), 8080);
        vertx.createHttpServer().requestHandler(router::accept).listen(port, res -> {
            if (res.succeeded()) {
                RemoteLoggerSrvLogger.LOG.listening(port);
                startFuture.complete();
            } else {
                RemoteLoggerSrvLogger.LOG.fatalListening(port, res.cause());
                startFuture.fail(res.cause());
            }
        });
    }

}
