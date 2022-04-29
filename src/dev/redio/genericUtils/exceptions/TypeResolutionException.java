package dev.redio.genericUtils.exceptions;

import java.lang.reflect.Type;

/**
 * Thrown to indicate that the resolution of a type failed.
 */
public class TypeResolutionException extends RuntimeException {
    public TypeResolutionException() {
    }

    public TypeResolutionException(String message) {
        super(message);
    }

    public TypeResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeResolutionException(Throwable cause) {
        super(cause);
    }

    public TypeResolutionException(Type type) {
        super("Type " + type + " couldn't be resolved to a class.");
    }
}
