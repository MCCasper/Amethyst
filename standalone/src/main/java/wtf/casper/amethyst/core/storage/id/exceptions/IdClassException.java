package wtf.casper.amethyst.core.storage.id.exceptions;

public final class IdClassException extends Exception {

    public IdClassException(final Class<?> type) {
        super("Expected class type: java.lang.String, java.util.UUID but got: " + type.getName());
    }

}
