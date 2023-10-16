package no.shhsoft.resourcetrav;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class ClassAnnotationResourceHandler
extends AbstractClassResourceHandler {

    private final Class<? extends Annotation> annotationClass;

    protected abstract boolean handleAnnotatedClass(Class<?> clazz);

    @Override
    protected final boolean handleClass(final Class<?> clazz) {
        if (clazz.getAnnotation(annotationClass) != null) {
            return handleAnnotatedClass(clazz);
        }
        return true;
    }

    protected ClassAnnotationResourceHandler(final Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

}
