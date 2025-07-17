package com.desoi.structra.util;

import com.desoi.structra.model.StructraException;
import org.jetbrains.annotations.NotNull;

public class Validate {

    static public void validate(boolean b, @NotNull String errorMessage) {
        if(!b) {
            throw new StructraException(errorMessage);
        }
    }

    static public void validate(@NotNull ThrowingSupplier<Boolean> supplier, @NotNull String errorMessage, boolean log) {
        try {
            if(!supplier.get()) {
                throw new StructraException(errorMessage);
            }
        }catch (Exception e) {
            if(log) errorMessage = errorMessage + e;
            throw new StructraException(errorMessage);
        }
    }

    static public void validate(@NotNull ThrowingSupplier<Boolean> supplier, @NotNull String errorMessage) {
        validate(supplier, errorMessage, false);
    }

    @NotNull
    static public <T> T validateException(@NotNull ThrowingSupplier<T> supplier, @NotNull String errorMessage, boolean log) {
        try {
            return supplier.get();
        }catch (Exception e) {
            if(log) errorMessage = errorMessage + e;
            throw new StructraException(errorMessage);
        }
    }

    @NotNull
    static public <T> T validateException(@NotNull ThrowingSupplier<T> supplier, @NotNull String errorMessage) {
        return validateException(supplier, errorMessage, false);
    }
}
