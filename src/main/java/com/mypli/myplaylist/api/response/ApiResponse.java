package com.mypli.myplaylist.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final static int SUCCESS = 200;
    private final static int BAD_REQUEST = 400;
    private final static int UNAUTHORIZED = 401;
    private final static int FORBIDDEN = 403;
    private final static int NOT_FOUND = 404;
    private final static int FAILED = 500;

    private final static String SUCCESS_MESSAGE = "SUCCESS";
    private final static String BAD_REQUEST_MESSAGE = "BAD REQUEST";
    private final static String UNAUTHORIZED_MESSAGE = "UNAUTHORIZED";
    private final static String FORBIDDEN_MESSAGE = "FORBIDDEN";
    private final static String NOT_FOUND_MESSAGE = "NOT FOUND";
    private final static String FAILED_MESSAGE = "서버에서 오류가 발생하였습니다.";
    private final static String INVALID_ACCESS_TOKEN = "Invalid access token.";
    private final static String INVALID_REFRESH_TOKEN = "Invalid refresh token.";
    private final static String NOT_EXPIRED_TOKEN_YET = "Not expired token yet.";

    private final ApiResponseHeader header;
    private final Map<String, T> body;

    public static <T> ApiResponse<T> OK() {
        return new ApiResponse(new ApiResponseHeader(SUCCESS, SUCCESS_MESSAGE), null);
    }

    public static <T> ApiResponse<T> success(String name, T body) {
        Map<String, T> map = new HashMap<>();
        map.put(name, body);

        return new ApiResponse(new ApiResponseHeader(SUCCESS, SUCCESS_MESSAGE), map);
    }

    public static <T> ApiResponse<T> badRequest() {
        return new ApiResponse(new ApiResponseHeader(BAD_REQUEST, BAD_REQUEST_MESSAGE), null);
    }

    public static <T> ApiResponse<T> unauthorized() {
        return new ApiResponse(new ApiResponseHeader(UNAUTHORIZED, UNAUTHORIZED_MESSAGE), null);
    }

    public static <T> ApiResponse<T> unauthorized(String errorMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("error", errorMessage);

        return new ApiResponse(new ApiResponseHeader(UNAUTHORIZED, UNAUTHORIZED_MESSAGE), map);
    }

    public static <T> ApiResponse<T> forbidden() {
        return new ApiResponse(new ApiResponseHeader(FORBIDDEN, FORBIDDEN_MESSAGE), null);
    }

    public static <T> ApiResponse<T> notFound() {
        return new ApiResponse(new ApiResponseHeader(NOT_FOUND, NOT_FOUND_MESSAGE), null);
    }

    public static <T> ApiResponse<T> notFound(String errorMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("error", errorMessage);

        return new ApiResponse(new ApiResponseHeader(NOT_FOUND, NOT_FOUND_MESSAGE), map);
    }

    public static <T> ApiResponse<T> fail() {
        return new ApiResponse(new ApiResponseHeader(FAILED, FAILED_MESSAGE), null);
    }

    public static <T> ApiResponse<T> invalidAccessToken() {
        return new ApiResponse(new ApiResponseHeader(FAILED, INVALID_ACCESS_TOKEN), null);
    }

    public static <T> ApiResponse<T> invalidRefreshToken() {
        return new ApiResponse(new ApiResponseHeader(FAILED, INVALID_REFRESH_TOKEN), null);
    }

    public static <T> ApiResponse<T> notExpiredTokenYet() {
        return new ApiResponse(new ApiResponseHeader(FAILED, NOT_EXPIRED_TOKEN_YET), null);
    }

}
