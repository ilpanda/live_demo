package com.ilpanda.live_demo.base.http.exception;

import java.io.IOException;

public class UnExpectedResponseCodeException extends IOException {


    public UnExpectedResponseCodeException(String message) {
        super(message);
    }
}
