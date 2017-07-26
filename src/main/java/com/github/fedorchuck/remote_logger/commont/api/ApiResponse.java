package com.github.fedorchuck.remote_logger.commont.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Wraps around any response from REST API.
 */
public class ApiResponse<T> {
    @Getter
    boolean success;
    @Getter
    private T value;
    @Getter
    private ApiError error;

    @JsonCreator
    public ApiResponse(@JsonProperty("success") boolean success,
                       @JsonProperty("value") T value,
                       @JsonProperty("error") ApiError error) {
        this.success = success;
        this.value = value;
        this.error = error;
    }
}
