package com.github.fedorchuck.remote_logger.commont.api;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * API status codes.
 */
public enum ResponseCode {
    UNKNOWN(-1, 500),

    OK(200, 200),
    NOT_IMPLEMENTED(200, 200),
    BAD_REQUEST(400, 400),
    UNAUTHORIZED(401, 401),
    UNAUTHENTICATED(403, 403),
    NOT_FOUND(404, 404),
    INTERNAL_ERROR(500, 500),
    VALIDATION_ERROR(600, 400),
    JSON_PARSE_ERROR(601, 400),
    ID_IS_ABSENT(602, 400),
    ID_FORMAT_ERROR(603, 400),
    OBJ_ID_DO_NOT_MATCH_REQUEST_URL(604, 400),
    TOKEN_IS_ABSENT(604, 400),
    ;
    private int code;
    private int httpCode;

    ResponseCode(int code, int httpCode) {
        this.code = code;
        this.httpCode = httpCode;
    }

    @JsonCreator
    public static ResponseCode fromString(String value) {
        for (ResponseCode grade : values()) {
            if (grade.name().equalsIgnoreCase(value)) {
                return grade;
            }
        }

        return UNKNOWN;
    }

    public int getCode() {
        return code;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
