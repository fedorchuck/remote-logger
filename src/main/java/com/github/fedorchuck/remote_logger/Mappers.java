package com.github.fedorchuck.remote_logger;

import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author fedorchuck.
 */
public class Mappers {
    public static UsersLogger convertJsonObjectToUsersLogger(JsonObject row) {
        return UsersLogger.builder()
                .recordId(row.getLong("record_id"))
                .accountId(row.getLong("account_id"))
                .collectionName(row.getString("collection_name"))
                .accessToken(row.getString("access_token"))
                .createdTs(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(Instant.parse(row.getString("created_at")).toEpochMilli()),
                        ZoneId.of("UTC")))
                .build();
    }

}
