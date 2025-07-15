package com.desoi.structra.model;

public class StructraException extends RuntimeException {

    public StructraException(String message) {
        super("[Structra] " + message);
    }
}
