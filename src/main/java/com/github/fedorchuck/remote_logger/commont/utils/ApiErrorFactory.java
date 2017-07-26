package com.github.fedorchuck.remote_logger.commont.utils;

import com.github.fedorchuck.remote_logger.commont.api.ApiError;
import com.github.fedorchuck.remote_logger.commont.api.ExceptionInfo;
import com.github.fedorchuck.remote_logger.commont.api.ResponseCode;
import com.github.fedorchuck.remote_logger.commont.api.ValidationError;
import com.github.fedorchuck.remote_logger.util.NullCheckUtil;

import javax.validation.ConstraintViolation;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
public class ApiErrorFactory {
    public static ApiError get(ResponseCode responseCode) {
        ApiError apiError = new ApiError();
        apiError.setStatusCode(responseCode);
        return apiError;
    }

    public static ApiError get(ResponseCode responseCode, Throwable throwable) {
        ApiError apiError = new ApiError();
        apiError.setStatusCode(responseCode);
        apiError.setException(buildExceptionInfo(throwable));
        return apiError;
    }

    public static ApiError get(ResponseCode responseCode, ExceptionInfo exceptionInfo) {
        ApiError apiError = new ApiError();
        apiError.setStatusCode(responseCode);
        apiError.setException(exceptionInfo);
        return apiError;
    }

    public static ApiError get(ResponseCode responseCode, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatusCode(responseCode);
        apiError.setMessage(message);
        return apiError;
    }

    public static <T> ApiError getValidationError(Set<ConstraintViolation<T>> violations) {
        ApiError apiError = new ApiError();
        apiError.setStatusCode(ResponseCode.VALIDATION_ERROR);
        apiError.setViolations(ValidationError.fromViolations(violations));
        return apiError;
    }

    private static ExceptionInfo buildExceptionInfo(Throwable throwable) {
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        exceptionInfo.setMessage(throwable.getMessage());

        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        exceptionInfo.setStackTrace(stringWriter.toString());

        List<String> causeMessages = new ArrayList<>();
        do {
            if (throwable.getCause() != null && !NullCheckUtil.isNullOrEmpty(throwable.getCause().getMessage())) {
                causeMessages.add(throwable.getCause().getMessage());
                throwable = throwable.getCause();
            }
        }
        while (throwable.getCause() != null);

        exceptionInfo.setCauseMessages(causeMessages);

        exceptionInfo.setSuppressed(
                Stream.of(throwable.getSuppressed())
                        .map(ApiErrorFactory::buildExceptionInfo)
                        .collect(Collectors.toList())
        );

        return exceptionInfo;
    }
}
