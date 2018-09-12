package com.edium.library.payload;

import org.springframework.http.HttpStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ApiErrorsView {
    private int status;
    private String message;
    private String timestamp;
    private String path;
    private String error;
    private List<ApiFieldError> detailErrors;

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public ApiErrorsView(HttpStatus status, String message, String path, List<ApiFieldError> detailErrors) {
        this.status = status.value();
        this.message = message;
        this.timestamp = df.format(new Date());
        this.path = path;
        this.error = status.name();
        this.detailErrors = detailErrors;
    }

    public ApiErrorsView(int status, String message, String path, String error, List<ApiFieldError> detailErrors) {
        this.status = status;
        this.message = message;
        this.timestamp = df.format(new Date());
        this.path = path;
        this.error = error;
        this.detailErrors = detailErrors;
    }

    public ApiErrorsView(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.message = message;
        this.timestamp = df.format(new Date());
        this.path = path;
        this.error = status.name();
    }

    public ApiErrorsView(int status, String message, String path, String error) {
        this.status = status;
        this.message = message;
        this.timestamp = df.format(new Date());
        this.path = path;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ApiFieldError> getDetailErrors() {
        return detailErrors;
    }

    public void setDetailErrors(List<ApiFieldError> detailErrors) {
        this.detailErrors = detailErrors;
    }
}
