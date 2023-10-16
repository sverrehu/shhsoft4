package no.shhsoft.web.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PaginationUtils {

    private PaginationUtils() {
        /* not to be instantiated */
    }

    public static int adjustFrom(final int oldFrom, final int totalNumberOfItems, final int itemsPerPage) {
        int from = oldFrom - 1;
        if (from < 0) {
            from = 0;
        }
        if (from >= totalNumberOfItems) {
            from = totalNumberOfItems - 1;
        }
        from = ((from / itemsPerPage)) * itemsPerPage;
        return from + 1;
    }

}
