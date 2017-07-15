package com.github.fedorchuck.remote_logger.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import lombok.Getter;

import java.io.IOException;

/**
 * Allows to send custom types of messages along Event-buss
 * Created by Bogdan Mart on 14.07.2016.
 */
public class JacksonMessageCodec<T> implements MessageCodec<T, T> {
    @Getter
    private final Class<T> aClass;

    JacksonMessageCodec(Class<T> aClass) {
        this.aClass = aClass;
    }

    @Override
    public void encodeToWire(Buffer buffer, T t) {
        String str = null;
        try {
            str = Json.mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        buffer.appendString(str, "UTF-8");
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        buffer = buffer.getBuffer(pos, buffer.length() - 1);
        String str = buffer.toString("UTF-8");
        try {
            return Json.mapper.readValue(str, aClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T transform(T t) {
        return t;
    }

    @Override
    public String name() {
        return aClass.getCanonicalName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
