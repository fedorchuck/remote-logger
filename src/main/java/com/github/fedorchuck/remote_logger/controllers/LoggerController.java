package com.github.fedorchuck.remote_logger.controllers;

import com.github.fedorchuck.remote_logger.RemoteLoggerSrvLogger;
import com.github.fedorchuck.remote_logger.commont.api.ApiResponse;
import com.github.fedorchuck.remote_logger.commont.api.ResponseCode;
import com.github.fedorchuck.remote_logger.commont.api.Responses;
import com.github.fedorchuck.remote_logger.commont.utils.ApiResponseFactory;
import com.github.fedorchuck.remote_logger.dal.UsersDataDatabase;
import com.github.fedorchuck.remote_logger.dal.UsersLogsDatabase;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogs;
import com.github.fedorchuck.remote_logger.util.HttpStatus;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author fedorchuck.
 */
public class LoggerController {
    private static RemoteLoggerSrvLogger LOG = RemoteLoggerSrvLogger.LOG;
    @Getter
    private Router router;
    private EventBus eventBus;

    public LoggerController(Vertx vertx) {
        router = Router.router(vertx);
        eventBus = vertx.eventBus();

        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.GET, "/v0/:accessToken").handler(this::get);
        router.route(HttpMethod.POST, "/v0/:accessToken").handler(this::post);
        router.route(HttpMethod.PUT, "/v0/:accessToken").handler(this::create);
    }

    /**
     * URL: /log/v0/:accessToken
     **/
    private void get(RoutingContext routingContext) {
        String accessToken = routingContext.request().params().get("accessToken");

        LOG.warnNotImplemented("URL: /log/v0/:accessToken");
        Responses.json(routingContext, 200, ApiResponseFactory.successWithValue(ResponseCode.NOT_IMPLEMENTED));
    }

    /**
     * URL: /log/v0/:accessToken
     **/
    private void post(RoutingContext routingContext) {
        String accessToken = routingContext.request().params().get("accessToken");

        //TODO: add token validation
        //TODO: move to bl
        eventBus.send(UsersDataDatabase.Address.readByAccessToken.address(),
                accessToken,
                res -> {
                    if (res.failed()) {
                        LOG.warnFailedToGetAccountInfo(accessToken, res.cause());
                        Responses.json(routingContext, 500, ApiResponseFactory.fail(res.cause()));
                        return;
                    }

                    UsersLogs userLogs = UsersLogs.builder()
                            .collectionName(((UsersLogger) res.result().body()).getCollectionName())
                            .data(routingContext.getBodyAsJson())
                            .build();
                    eventBus.send(UsersLogsDatabase.Address.create.address(),
                            userLogs,
                            createMongoEntry -> {
                                if (createMongoEntry.failed()) {
                                    LOG.warnFailedToCreateMongoDocument(accessToken, res.cause());
                                    Responses.json(routingContext, 500, ApiResponseFactory.fail(res.cause()));
                                    return;
                                }

                                Responses.json(routingContext, 200, (ApiResponse) createMongoEntry.result().body());
                            });
                });
    }

    /**
     * URL: /log/v0/:accessToken
     * Should be not here
     **/
    @Deprecated
    private void create(RoutingContext routingContext) {
        String accessToken = routingContext.request().params().get("accessToken");

        UsersLogger usersLogger = UsersLogger.builder()
                .accountId(1L)
                .accessToken("qwerty")
                .collectionName("test")
                .createdTs(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
                .build();
        eventBus.send(UsersDataDatabase.Address.create.address(),
                usersLogger, res -> {
                    int a = 5;
                });


        LOG.warnNotImplemented("URL: /log/v0/:accessToken");
        Responses.json(routingContext, 200, ApiResponseFactory.successWithValue(ResponseCode.NOT_IMPLEMENTED));
    }
}
