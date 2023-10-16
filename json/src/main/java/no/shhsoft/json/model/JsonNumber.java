package no.shhsoft.json.model;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface JsonNumber
extends JsonValue {

    double getValueAsDouble();

    long getValueAsLong();

}
