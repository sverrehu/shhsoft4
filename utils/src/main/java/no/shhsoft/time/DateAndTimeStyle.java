package no.shhsoft.time;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public enum DateAndTimeStyle {

    ISO_8601,
    NORWEGIAN,
    DANISH,
    SWEDISH,

    /* Variants used for parsing only */
    ISO_8601_SHORT_TIME,
    ISO_8601_DOT_TIME,
    NORWEGIAN_COLON_TIME,
    NORWEGIAN_DOT_TIME,
    SWEDISH_WITH_DASH_BEFORE_YEAR,

}
