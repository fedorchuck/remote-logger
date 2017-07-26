package com.github.fedorchuck.remote_logger.commont.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fedorchuck.remote_logger.infrastructure.EventBusParameter;
import lombok.Getter;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWrapper<T> implements EventBusParameter {
    private String message;
    private T value;

    @JsonCreator
    public ResponseWrapper(@JsonProperty("message") String message,
                       @JsonProperty("value") T value) {
        this.message = message;
        this.value = value;
    }
}
