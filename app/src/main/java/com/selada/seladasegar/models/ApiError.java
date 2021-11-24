package com.selada.seladasegar.models;

import com.google.gson.annotations.SerializedName;

public class ApiError {

    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
