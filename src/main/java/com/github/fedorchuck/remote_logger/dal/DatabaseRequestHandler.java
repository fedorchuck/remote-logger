package com.github.fedorchuck.remote_logger.dal;

import com.github.fedorchuck.remote_logger.Mappers;
import com.github.fedorchuck.remote_logger.RemoteLoggerSrvLogger;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogs;
import com.github.fedorchuck.remote_logger.util.HttpStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
class DatabaseRequestHandler {
    private static RemoteLoggerSrvLogger LOG = RemoteLoggerSrvLogger.LOG;

    static void selectRow(SQLConnection connection, Message message, AsyncResult<ResultSet> res, Object database) {
        if (res.failed()) {
            connection.close();
            LOG.errorDatabaseFailed(res.cause());
            message.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, res.cause().getMessage());
            return;
        }
        List<JsonObject> rs = res.result().getRows();
        if (rs.size() > 1 || rs.size() == 0) {
            connection.close();
            LOG.errorDatabaseFailed(res.cause());
            message.fail(HttpStatus.SC_BAD_REQUEST, "Invalid size " + rs.size());
            return;
        }

        if (database instanceof UsersDataDatabase) {
            UsersLogger usersLogger =
                    Mappers.convertJsonObjectToUsersLogger(rs.get(0));
            connection.close();
            LOG.traceUsersDataEntryFromDatabase(usersLogger);
            message.reply(usersLogger);
        }
    }

    static void selectList(SQLConnection connection, Message message, AsyncResult<ResultSet> res, Object database) {
        if (res.failed()) {
            connection.close();
            message.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, res.cause().getMessage());
            return;
        }
        List<JsonObject> rs = res.result().getRows();
//        if (database instanceof ApplicationsDatabase) {
//            List<MarketplaceApp> apps = new ArrayList<>();
//            for (JsonObject jo : rs) {
//                apps.add(Mappers.convertJsonObjectToMarketplaceApp(jo));
//                LOG.traceFromDatabase(Mappers.convertJsonObjectToMarketplaceApp(jo));
//            }
//            connection.close();
//            message.reply(Json.encode(apps));
//        }
        if (database instanceof UsersDataDatabase) {
            List<UsersLogger> usersData = new ArrayList<>();
            for (JsonObject jo : rs) {
                //noinspection unchecked
                usersData.add(Mappers.convertJsonObjectToUsersLogger(jo));
                LOG.traceUsersDataEntryFromDatabase(Mappers.convertJsonObjectToUsersLogger(jo));
            }
            connection.close();
//            EventbusParam<List<UsersLogger>> result = new EventbusParam<>();
//            result.setParamValue(usersData);
//            message.reply(result);
        }
    }

    static void connectionFail(AsyncResult<SQLConnection> conn, Message message) {
        conn.result().close();
        LOG.warnPostgresqlConnectionFail(conn.cause());
        message.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, "connection failed");
    }

    static void operationFailed(Message message, UsersLogs entry, Throwable cause) {
        LOG.warnMongoConnectionFail(entry, cause);
        message.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, cause.getMessage());
    }

    static void nullResult(Message message, UsersLogs entry) {
        LOG.warnMongoNullResult(entry);
        message.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, "result is null");
    }
}
