package com.github.fedorchuck.remote_logger.dal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.fedorchuck.remote_logger.RemoteLoggerSrvLogger;
import com.github.fedorchuck.remote_logger.util.DatabaseUtil;
import com.github.fedorchuck.remote_logger.util.NullCheckUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.exception.LiquibaseException;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Getter;

import java.util.LinkedHashMap;

/**
 * @author fedorchuck.
 */
public class LiquibaseVerticle extends AbstractVerticle {
    private static final String SCHEMA_VERSION = "v/0.1.0";
    private RemoteLoggerSrvLogger LOG = RemoteLoggerSrvLogger.LOG;
    @Getter
    private JDBCClient client;
    private SqlParams params;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (NullCheckUtil.isNullOrEmpty(SCHEMA_VERSION)) {
            LOG.errorDatabaseError(new Error("SCHEMA_VERSION isn't set."));
            throw new Error("SCHEMA_VERSION isn't set.");
        }

        LinkedHashMap<String, Object> map = DatabaseUtil.getPostgresqlDatabaseKeys();
        params = Json.decodeValue(Json.encode(map), LiquibaseVerticle.SqlParams.class);
        client = JDBCClient.createShared(vertx, new JsonObject(map));

        vertx.executeBlocking(
                (Future<Void> future) -> {
                    try {
                        runLiquibase();
                        future.complete();
                    } catch (Throwable e) {
                        LOG.errorDatabaseError(e);
                        future.fail(e);
                    }
                }, (AsyncResult<Void> result) -> {
                    if (result.failed()) {
                        LOG.errorLiquibaseFailed(result.cause());
                        startFuture.fail(result.cause());
                    } else {
                        startFuture.complete();
                        LOG.liquibaseUpdated(SCHEMA_VERSION);
                    }
                }
        );
    }

    private void runLiquibase() throws LiquibaseException {
        Database database = CommandLineUtils.createDatabaseObject(new ClassLoaderResourceAccessor(), params.url, params.user, params.password, params.driver_class, params.catalog, params.schema, false, false, null, null, null, null, null, null, null);

        Liquibase liquibase = new Liquibase("db/master.xml", new ClassLoaderResourceAccessor(), database);
        // update to current V
        if (!liquibase.tagExists(SCHEMA_VERSION)) {
            liquibase.updateTestingRollback(SCHEMA_VERSION, new Contexts(), new LabelExpression());
        }
    }

    @SuppressWarnings("WeakerAccess")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SqlParams {
        public String url;
        public String user;
        public String password;
        public String driver_class;
        public String catalog;
        public String schema;
    }
}
