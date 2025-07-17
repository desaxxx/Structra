package com.desoi.structra.util;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
