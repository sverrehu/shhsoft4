package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface StringConverter<T> {

    boolean isValid(String s);

    T convertFromString(String s);

    String convertToString(T t);

}
