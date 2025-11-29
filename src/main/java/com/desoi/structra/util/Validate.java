package com.desoi.structra.util;

import com.desoi.structra.model.StructraException;
import org.jetbrains.annotations.NotNull;

public class Validate {

    public static void validate(boolean b, @NotNull String errorMessage) {
        if(!b) {
            throw new StructraException(errorMessage);
        }
    }

    public static void validate(@NotNull ThrowingSupplier<Boolean> supplier, @NotNull String errorMessage, boolean log) {
        try {
            if(!supplier.get()) {
                throw new StructraException(errorMessage);
            }
        }catch (Exception e) {
            if(log) errorMessage = errorMessage + e;
            throw new StructraException(errorMessage);
        }
    }

    public static void validate(@NotNull ThrowingSupplier<Boolean> supplier, @NotNull String errorMessage) {
        validate(supplier, errorMessage, false);
    }

    @NotNull
    public static <T> T validateException(@NotNull ThrowingSupplier<T> supplier, @NotNull String errorMessage, boolean log) {
        try {
            return supplier.get();
        }catch (Exception e) {
            if(log) errorMessage = errorMessage + e;
            throw new StructraException(errorMessage);
        }
    }

    @NotNull
    public static <T> T validateException(@NotNull ThrowingSupplier<T> supplier, @NotNull String errorMessage) {
        return validateException(supplier, errorMessage, false);
    }

    @NotNull
    public static <T> T notNull(T object, @NotNull String errorMessage) {
        if(object == null) {
            throw new StructraException(errorMessage);
        }
        return object;
    }

    public static void notNull(Object object1, Object object2, @NotNull String errorMessage) {
        if(object1 == null || object2 == null) {
            throw new StructraException(errorMessage);
        }
    }
    public static void notNull(Object object1, Object object2, Object object3, @NotNull String errorMessage) {
        if(object1 == null || object2 == null || object3 == null) {
            throw new StructraException(errorMessage);
        }
    }
}
