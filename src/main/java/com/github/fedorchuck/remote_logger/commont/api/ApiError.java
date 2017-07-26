package com.github.fedorchuck.remote_logger.commont.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError<T> {
    private ResponseCode statusCode;
    private String statusCodeName;
    private int statusCodeValue;
    private String message;
    private ExceptionInfo exception;
    private Set<ValidationError> violations;

    public void setStatusCode(ResponseCode statusCode) {
        this.statusCode = statusCode;
        this.statusCodeName = statusCode.name();
        this.statusCodeValue = statusCode.getCode();
    }

}
