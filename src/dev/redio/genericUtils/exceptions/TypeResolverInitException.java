package dev.redio.genericUtils.exceptions;

/**
 * Thrown to indicate that the type resolver couldn't be initialized.
 */
public class TypeResolverInitException extends RuntimeException {
    public TypeResolverInitException() {
    }

    public TypeResolverInitException(String message) {
        super(message);
    }

    public TypeResolverInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeResolverInitException(Throwable cause) {
        super(cause);
    }
}
