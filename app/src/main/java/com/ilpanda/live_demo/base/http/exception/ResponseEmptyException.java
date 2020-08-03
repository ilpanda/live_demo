package com.ilpanda.live_demo.base.http.exception;

import java.io.IOException;

public class ResponseEmptyException extends IOException {


    public ResponseEmptyException(String message) {
        super(message);
    }
}
