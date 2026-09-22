package com.desoi.structra.model;

import com.desoi.structra.util.Util;

public class StructraException extends RuntimeException {

    public StructraException(String message) {
        super(Util.PREFIX + message);
    }

    public StructraException(String message, Throwable cause) {
        super(Util.PREFIX + message, cause);
    }
}
