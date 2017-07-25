package com.github.fedorchuck.remote_logger.util;

import com.github.fedorchuck.remote_logger.RemoteLoggerSrvLogger;
import com.github.fedorchuck.remote_logger.dal.model.PgConfig;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
public class DatabaseUtil {
    private final RemoteLoggerSrvLogger LOG = RemoteLoggerSrvLogger.LOG;

    public LinkedHashMap<String, Object> getPostgresqlDatabaseKeys() {
        String pgConfigAsString = System.getenv("PG_CONFIG");

        if (NullCheckUtil.isNullOrEmpty(pgConfigAsString)) {
            LOG.warnEnvironmentVariableNotFound("PG_CONFIG");
            pgConfigAsString = getProperties("PG_CONFIG");

            if (NullCheckUtil.isNullOrEmpty(pgConfigAsString)) {
                LOG.errorEnvironmentVariableNotFound("PG_CONFIG");
                throw new IllegalArgumentException("PG_CONFIG was not found.");
            }
        }

        PgConfig pgConfig;
        try {
            pgConfig = Json.decodeValue(pgConfigAsString, PgConfig.class);
        } catch (DecodeException ex) {
            if (ex.getMessage().contains("Failed to decode")) {
                LOG.errorReadProperty("PG_CONFIG", ex);
                throw new IllegalArgumentException("PG_CONFIG has invalid format; Exception: ", ex);
            } else {
                LOG.errorItShouldNeverHappen(ex);
                throw new IllegalArgumentException(ex);
            }
        }

        LinkedHashMap<String, Object> map = new LinkedHashMap<>(6);
        map.put("url", pgConfig.getPgUrl());
        map.put("driver_class", pgConfig.getPgDriver());
        map.put("max_pool_size", 30);
        map.put("user", pgConfig.getPgUser());
        map.put("password", pgConfig.getPgPassword());
        map.put("max_idle_time", 120);
        return map;
    }

    public JsonObject getMongoConfig() {
        String uri = System.getenv("MONGO_URI");

        if (NullCheckUtil.isNullOrEmpty(uri)) {
            LOG.warnEnvironmentVariableNotFound("MONGO_URI");
            uri = getProperties("MONGO_URI");

            if (NullCheckUtil.isNullOrEmpty(uri)) {
                LOG.errorEnvironmentVariableNotFound("MONGO_URI");
                throw new IllegalArgumentException("MONGO_URI was not found.");
            }
        }

        return new JsonObject()
                .put("connection_string", uri);
    }

    /**
     * Return loaded properties from file <l>gradle.properties</l>
     *
     * @param propertyName name of property to find
     * @return null if property file was not found or if happens {@link IOException}
     **/
    public String getProperties(String propertyName) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("gradle.properties");
            prop.load(input);

            return prop.getProperty(propertyName);
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            LOG.warnItShouldNeverHappen(ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOG.warnItShouldNeverHappen(e);
                }
            }
        }
    }

}
