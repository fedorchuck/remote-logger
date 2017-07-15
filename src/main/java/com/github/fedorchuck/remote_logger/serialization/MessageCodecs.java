package com.github.fedorchuck.remote_logger.serialization;

import io.vertx.core.eventbus.EventBus;

import java.util.List;

/**
 * Created by Bogdan Mart on 14.07.2016.
 */
@SuppressWarnings({"unchecked", "DefaultFileTemplate"})
public class MessageCodecs {

    @SuppressWarnings("WeakerAccess")
    public static final JacksonMessageCodec[] allCodecs = new JacksonMessageCodec[]{
            new JacksonMessageCodec(com.github.fedorchuck.remote_logger.dal.model.UsersLogger.class),
            new JacksonMessageCodec(com.github.fedorchuck.remote_logger.dal.model.UsersLogs.class),

            new JacksonMessageCodec(List.class)
    };

    public static void registerAllCodecs(EventBus buss) {
        for (JacksonMessageCodec codec : allCodecs) {
            buss.registerDefaultCodec(codec.getAClass(), codec);
        }
    }
}
