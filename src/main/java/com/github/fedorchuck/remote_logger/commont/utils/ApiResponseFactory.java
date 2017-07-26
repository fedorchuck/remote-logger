package com.github.fedorchuck.remote_logger.commont.utils;


import com.github.fedorchuck.remote_logger.commont.api.ApiError;
import com.github.fedorchuck.remote_logger.commont.api.ApiResponse;
import com.github.fedorchuck.remote_logger.commont.api.ExceptionInfo;
import com.github.fedorchuck.remote_logger.commont.api.ResponseCode;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;

/**
 * Used to create {@link com.github.fedorchuck.remote_logger.commont.api.ApiResponse API Responses}
 */
public class ApiResponseFactory {
    public static ApiResponse<Void> successNoValue() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> successWithValue(T value) {
        return new ApiResponse<T>(true, value, null);
    }

    public static <T> ApiResponse<T> fail(ApiError error) {
        return new ApiResponse<T>(false, null, error);
    }

    public static <T> ApiResponse<T> fail(ResponseCode responseCode) {
        return fail(ApiErrorFactory.get(responseCode));
    }

    public static <T> ApiResponse<T> fail(Throwable throwable) {
        return fail(ApiErrorFactory.get(ResponseCode.INTERNAL_ERROR, throwable));
    }

    public static <T> ApiResponse<T> fail(ResponseCode responseCode, Throwable throwable) {
        return fail(ApiErrorFactory.get(responseCode, throwable));
    }

    public static <T> ApiResponse<T> fail(ResponseCode responseCode, String message) {
        return fail(ApiErrorFactory.get(responseCode, message));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> ApiResponse<T> apiRespFromOptional(Optional<T> value) {
        if (value.isPresent()) {
            return successWithValue(value.get());
        }
        return fail(ApiErrorFactory.get(ResponseCode.NOT_FOUND));
    }

    public static <T> ApiResponse failWithValidationError(Set<ConstraintViolation<T>> violations) {
        return fail(ApiErrorFactory.getValidationError(violations));
    }

    public <T> ApiResponse<T> fail(ResponseCode responseCode, ExceptionInfo exceptionInfo) {
        return fail(ApiErrorFactory.get(responseCode, exceptionInfo));
    }
}
