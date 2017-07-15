package com.github.fedorchuck.remote_logger.dal;

import com.github.fedorchuck.jsqlb.Column;
import com.github.fedorchuck.jsqlb.JSQLBuilder;
import com.github.fedorchuck.jsqlb.Table;
import com.github.fedorchuck.jsqlb.postgresql.PGConditionalExpression;
import com.github.fedorchuck.jsqlb.postgresql.PGDataTypes;
import com.github.fedorchuck.jsqlb.postgresql.PostgreSQL;
import com.github.fedorchuck.remote_logger.RemoteLoggerSrvLogger;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import com.github.fedorchuck.remote_logger.infrastructure.EventBusAddressDescriptor;
import com.github.fedorchuck.remote_logger.util.DatabaseUtil;
import com.github.fedorchuck.remote_logger.util.HttpStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

public class UsersDataDatabase extends AbstractVerticle {
    private static final String BASE_ADDRESS = UsersDataDatabase.class.getCanonicalName();
    private RemoteLoggerSrvLogger LOG = RemoteLoggerSrvLogger.LOG;
    private JDBCClient client;
    private Table table;
    private JSQLBuilder jsqlBuilder;

    @Override
    public void start() throws Exception {
        client = JDBCClient.createShared(vertx, new JsonObject(DatabaseUtil.getPostgresqlDatabaseKeys()));

        jsqlBuilder = new PostgreSQL();
        table = new Table("users_data");
        table.addColumn(new Column("id", PGDataTypes.INT));
        table.addColumn(new Column("account_id", PGDataTypes.INT));
        table.addColumn(new Column("collection_name", PGDataTypes.TEXT));
        table.addColumn(new Column("access_token", PGDataTypes.TEXT));
        table.addColumn(new Column("created_at", PGDataTypes.TIMESTAMP));

        vertx.eventBus().consumer(Address.create.address()).handler(this::create);
        vertx.eventBus().consumer(Address.readByAccessToken.address()).handler(this::readByAccessToken);
    }

    private void create(Message message) {
        client.getConnection(conn -> {
            if (conn.failed()) {
                DatabaseRequestHandler.connectionFail(conn, message);
                return;
            }

            SQLConnection connection = conn.result();
            UsersLogger databaseEntry = (UsersLogger) message.body();
            LOG.traceCreateUsersDataEntry(databaseEntry);
//"INSERT INTO " + TABLE + " (" + FIELDS_FOR_INSERT_EX_ID + ") values (?, ?, ?, ?);
            connection.updateWithParams(
                    jsqlBuilder.insert(table, table.getColumnsExcept(table.getColumn("id"))).getSQL(),
                    new JsonArray(Arrays.asList(
                            databaseEntry.getAccountId(),
                            databaseEntry.getCollectionName(),
                            databaseEntry.getAccessToken(),
                            Timestamp.valueOf(databaseEntry.getCreatedTs())
                    )),
                    res -> {
                        if (res.failed()) {
                            connection.close();
                            LOG.warnFailedToCreateUsersDataEntry(databaseEntry, res.cause());
                            message.fail(HttpStatus.SC_INTERNAL_SERVER_ERROR, res.cause().getMessage());
                            return;
                        }
//"SELECT " + ALL_FIELDS_FOR_SELECT + " FROM " + TABLE + " WHERE id = ?;
                        connection.queryWithParams(
                                jsqlBuilder.select().from(table).where(new PGConditionalExpression(table.getColumn("id")).equal()).getSQL(),
                                res.result().getKeys(),
                                resAfterCreate ->
                                        DatabaseRequestHandler.selectRow(connection, message, resAfterCreate, this));
                    }
            );

        });
    }

    private void readByAccessToken(Message message) {
        client.getConnection(conn -> {
            if (conn.failed()) {
                DatabaseRequestHandler.connectionFail(conn, message);
                return;
            }

            SQLConnection connection = conn.result();
            String accountId = (String) message.body();
            LOG.traceReadByAccessTokenUsersData(accountId);
//"SELECT " + ALL_FIELDS_FOR_SELECT + " FROM " + TABLE + " WHERE access_token = ?;
            connection.queryWithParams(
                    jsqlBuilder.select().from(table).where(new PGConditionalExpression(table.getColumn("access_token")).equal()).getSQL(),
                    new JsonArray(Collections.singletonList(accountId)),
                    res -> DatabaseRequestHandler.selectRow(connection, message, res, this));
        });
    }

    @Override
    public void stop() throws Exception {
    }

    public enum Address implements EventBusAddressDescriptor {
        /**
         * <b>Create entry.</b>
         * <ul>
         * <li>Takes: {@link UsersLogger }</li>
         * <li>Returns: {@link UsersLogger}</li>
         * </ul>
         **/
        create,
        /**
         * <b>Get account data.</b>
         * <ul>
         * <li>Takes: {@link String remote-logger access token }</li>
         * <li>Returns: {@link UsersLogger}</li>
         * </ul>
         **/
        readByAccessToken,
        //TODO: readByAccountId
        ;

        public String address() {
            return BASE_ADDRESS + name();
        }
    }

}
