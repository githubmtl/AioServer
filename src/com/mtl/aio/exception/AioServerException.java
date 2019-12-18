package com.mtl.aio.exception;

public class AioServerException extends RuntimeException {
    public AioServerException() {
    }

    public AioServerException(String message) {
        super(message);
    }

    public AioServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AioServerException(Throwable cause) {
        super(cause);
    }

    public AioServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
