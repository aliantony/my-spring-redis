package com.example.myspringredis.util;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ObjectResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer code;

    public ObjectResponse() {
        this.success = true;
        this.message = "成功";
        this.code = 200;
    }

    public ObjectResponse(T data) {
        this.data = data;
        this.success = true;
        this.message = "成功";
        this.code = 200;
    }

    public static <T> ObjectResponse<T> success() {
        return new ObjectResponse<>();
    }

    public static <T> ObjectResponse success(T data) {
        return new ObjectResponse<>(data);
    }

    public static <T> ObjectResponse<T> fail(String message) {
        ObjectResponse r = new ObjectResponse<>();
        r.setCode(500);
        r.setSuccess(false);
        r.setMessage(message);
        return r;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}