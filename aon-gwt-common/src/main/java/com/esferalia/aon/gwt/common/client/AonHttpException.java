package com.esferalia.aon.gwt.common.client;

@SuppressWarnings("serial")
public class AonHttpException extends RuntimeException {

    private final int statusCode;

    public AonHttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}