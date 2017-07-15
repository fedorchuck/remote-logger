package com.github.fedorchuck.remote_logger.util;

import com.github.fedorchuck.remote_logger.dal.model.PgConfig;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.LinkedHashMap;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
public class DatabaseUtil {

    public static LinkedHashMap<String, Object> getPostgresqlDatabaseKeys() {
        PgConfig pgConfig = Json.decodeValue(System.getenv("PG_CONFIG"), PgConfig.class);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>(6);
        map.put("url", pgConfig.getPgUrl());
        map.put("driver_class", pgConfig.getPgDriver());
        map.put("max_pool_size", 30);
        map.put("user", pgConfig.getPgUser());
        map.put("password", pgConfig.getPgPassword());
        map.put("max_idle_time", 120);
        return map;
    }

    public static JsonObject getMongoConfig() {
        String uri = System.getenv("MONGO_URI");
        //TODO: what if null or empty?
        return new JsonObject()
                .put("connection_string", uri);
    }

}
