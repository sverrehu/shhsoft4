package no.shhsoft.resourcetrav;

import java.net.URL;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Resource {

    private final ResourceTraverser traverser;
    private final Type type;
    private final URL url;
    private final String name;

    enum Type {
        FILE,
        JAR,
    }

    Resource(final ResourceTraverser traverser, final Type type, final URL url, final String name) {
        this.traverser = traverser;
        this.type = type;
        this.url = url;
        this.name = name;
    }

    Type getType() {
        return type;
    }

    public URL getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return traverser.getContent(this);
    }

}
