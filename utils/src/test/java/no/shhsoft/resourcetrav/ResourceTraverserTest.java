package no.shhsoft.resourcetrav;

import no.shhsoft.resourcetrav.testclasses.AnnotatedClass;
import no.shhsoft.resourcetrav.testclasses.AnnotationOne;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ResourceTraverserTest {

    private List<String> loadResourceNames(final int max) {
        final List<String> names = new ArrayList<>();
        new ResourceTraverser().traverse(resource -> {
            if (names.size() >= max) {
                return false;
            }
            names.add(resource.getName());
            return true;
        }, "no/shhsoft");
        return names;
    }

    @Test
    public void shouldFindSomethingToTestWith() {
        assertTrue(loadResourceNames(Integer.MAX_VALUE).size() > 10);
    }

    @Test
    public void shouldStopWhenSignalled() {
        assertEquals(3, loadResourceNames(3).size());
    }

    @Test
    public void shouldFindAnnotatedClass() {
        final List<Class<?>> classes = new ArrayList<>();
        new ResourceTraverser().traverse(new ClassAnnotationResourceHandler(AnnotationOne.class) {
            @Override
            protected boolean handleAnnotatedClass(final Class<?> clazz) {
                classes.add(clazz);
                return true;
            }
        }, "no/shhsoft");
        assertEquals(1, classes.size());
        assertEquals(AnnotatedClass.class, classes.get(0));
    }

}
