package no.shhsoft.data;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface StateChangeListener<T> {

    void stateChanged(T t);

}
