package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UnknownEnumConstantException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnknownEnumConstantException(final Enum<?> enumValue) {
        super("Unknown enum constant `" + enumValue + "' in " + enumValue.getClass().getName());
    }

}
