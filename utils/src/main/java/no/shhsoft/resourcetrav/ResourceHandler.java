package no.shhsoft.resourcetrav;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface ResourceHandler {

    /**
     * @return <code>true</code> to continue traversing, <code>false</code> to terminate.
     */
    boolean handle(Resource resource);

}
