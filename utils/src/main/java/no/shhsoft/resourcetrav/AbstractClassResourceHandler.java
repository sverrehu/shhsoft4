package no.shhsoft.resourcetrav;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractClassResourceHandler
implements ResourceHandler {

    protected abstract boolean handleClass(Class<?> clazz);

    @Override
    public final boolean handle(final Resource resource) {
        final String name = resource.getName();
        if (!name.endsWith(".class")) {
            return true;
        }
        final String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
        final Class<?> clazz;
        try {
            clazz = getClass().getClassLoader().loadClass(className);
        } catch (final Throwable t) {
            System.err.println("Error loading class `" + className + "': " + t.getMessage());
            return true;
        }
        return handleClass(clazz);
    }

}
