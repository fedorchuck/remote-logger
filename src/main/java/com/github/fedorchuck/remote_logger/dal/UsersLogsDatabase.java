package com.github.fedorchuck.remote_logger.dal;

import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogs;
import com.github.fedorchuck.remote_logger.infrastructure.EventBusAddressDescriptor;
import com.github.fedorchuck.remote_logger.util.DatabaseUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientDeleteResult;

import java.util.List;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
public class UsersLogsDatabase extends AbstractVerticle {
    private static final String BASE_ADDRESS = UsersLogsDatabase.class.getCanonicalName();
    private MongoClient client;

    @Override
    public void start() throws Exception {
        client = MongoClient.createShared(vertx, new DatabaseUtil().getMongoConfig());

        vertx.eventBus().consumer(UsersLogsDatabase.Address.create.address()).handler(this::create);
        vertx.eventBus().consumer(UsersLogsDatabase.Address.read.address()).handler(this::read);
        vertx.eventBus().consumer(UsersLogsDatabase.Address.remove.address()).handler(this::remove);
        vertx.eventBus().consumer(UsersLogsDatabase.Address.delete.address()).handler(this::delete);
    }

    private void create(Message message) {
        UsersLogs entry = (UsersLogs) message.body();
        client.save(entry.getCollectionName(), entry.getData(), res -> {
            String insertedId = res.result();
            if (res.failed()) {
                DatabaseRequestHandler.operationFailed(message, entry, res.cause());
                return;
            }
            if (insertedId == null) {
                DatabaseRequestHandler.nullResult(message, entry);
                return;
            }
            message.reply(insertedId);
        });
    }

    private void read(Message message) {
        UsersLogs entry = (UsersLogs) message.body();
        client.find(entry.getCollectionName(), entry.getData(), res -> {
            List<JsonObject> result = res.result();
            if (res.failed()) {
                DatabaseRequestHandler.operationFailed(message, entry, res.cause());
                return;
            }
            if (result == null) {
                DatabaseRequestHandler.nullResult(message, entry);
                return;
            }
            if (result.size() != 1) {
                DatabaseRequestHandler.invalidSize(message, entry, result);
                return;
            }
            message.reply(result.get(0).toString());
        });
    }

    //remove query document
    private void remove(Message message) {
        UsersLogs entry = (UsersLogs) message.body();
        client.removeDocument(entry.getCollectionName(), entry.getData(), res -> {
            MongoClientDeleteResult result = res.result();
            if (res.failed()) {
                DatabaseRequestHandler.operationFailed(message, entry, res.cause());
                return;
            }
            if (result == null) {
                DatabaseRequestHandler.nullResult(message, entry);
                return;
            }
            //TODO: return object
            message.reply(result);
        });
    }

    //TODO: delete collection
    private void delete(Message message) {
        UsersLogs entry = (UsersLogs) message.body();
        client.dropCollection(entry.getCollectionName(), res -> {
            if (res.failed()) {
                DatabaseRequestHandler.operationFailed(message, entry, res.cause());
                return;
            }

            //TODO: return object
            message.reply("ok");
        });
    }

    public enum Address implements EventBusAddressDescriptor {
        /**
         * <b>Create entry.</b>
         * <ul>
         * <li>Takes: {@link UsersLogs }</li>
         * <li>Returns: {@link String inserted id}</li>
         * </ul>
         **/
        create,
        /**
         * <b>Get account data.</b>
         * <ul>
         * <li>Takes: {@link UsersLogs }</li>
         * <li>Returns: {@link String JSON}</li>
         * </ul>
         **/
        read,

        remove,

        delete,
        ;

        public String address() {
            return BASE_ADDRESS + name();
        }
    }
}
