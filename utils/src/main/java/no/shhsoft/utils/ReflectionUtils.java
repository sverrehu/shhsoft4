package no.shhsoft.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        /* not to be instantiated */
    }

    public static String createMethodName(final String prefix, final String propertyName) {
        return prefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    public static List<Method> findMethods(final Class<?> clazz, final String methodName, final int numArgs) {
        final List<Method> methods = new ArrayList<>();
        final Method[] allMethods = clazz.getMethods();
        for (final Method method : allMethods) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            if (numArgs >= 0 && method.getParameterTypes().length != numArgs) {
                continue;
            }
            methods.add(method);
        }
        return methods;
    }

    public static Method findMethod(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    public static Method findGetter(final Class<?> clazz, final String propertyName, final Class<?>[] parameterTypes) {
        Method method;
        try {
            method = clazz.getMethod(createMethodName("get", propertyName), parameterTypes);
        } catch (final NoSuchMethodException e) {
            try {
                method = clazz.getMethod(createMethodName("is", propertyName), parameterTypes);
            } catch (final NoSuchMethodException e2) {
                method = null;
            }
        }
        return method;
    }

    public static Method findGetter(final Class<?> clazz, final String propertyName) {
        return findGetter(clazz, propertyName, null);
    }

    public static Method findSetter(final Class<?> clazz, final String propertyName, final Class<?> propertyType) {
        Method method;
        try {
            method = clazz.getMethod(createMethodName("set", propertyName), propertyType);
        } catch (final NoSuchMethodException e) {
            method = null;
        }
        return method;
    }

    public static Method[] findSetters(final Class<?> clazz, final String propertyName) {
        final List<Method> methods = findMethods(clazz, createMethodName("set", propertyName), 1);
        return methods.toArray(new Method[0]);
    }

    public static Method[] findGetters(final Class<?> clazz, final String propertyName) {
        final List<Method> methods = findMethods(clazz, createMethodName("get", propertyName), 0);
        methods.addAll(findMethods(clazz, createMethodName("is", propertyName), 0));
        return methods.toArray(new Method[0]);
    }

    public static Object callGetter(final Object object, final String propertyName)
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = findGetter(object.getClass(), propertyName);
        if (method == null) {
            throw new NoSuchMethodException("Getter for `" + propertyName + "'");
        }
        return method.invoke(object, (Object[]) null);
    }

    public static Object callSetter(final Object object, final String propertyName, final Object value)
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<?> clazz = object.getClass();
        /*
         * Find the getter in order to determine the type. There may be several (convenience)
         * setters for various representations of the type, but only one getter.
         */
        final Method getter = findGetter(clazz, propertyName);
        if (getter == null) {
            throw new RuntimeException("Unable to determine type of property `" + propertyName
                                       + "'");
        }
        final Class<?> propertyType = getter.getReturnType();
        final Method method = findSetter(clazz, propertyName, propertyType);
        if (method == null) {
            throw new NoSuchMethodException("Setter for `" + propertyName + "'");
        }
        Object convertedValue = value;
        if (value instanceof String && propertyType != String.class) {
            convertedValue = StringConversionUtils.convertFromString((String) value, propertyType);
        }
        return method.invoke(object, convertedValue);
    }

    public static String getMethodSignature(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) {
        final StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName());
        sb.append('.');
        sb.append(methodName);
        sb.append('(');
        if (parameterTypes != null) {
            for (int q = 0; q < parameterTypes.length; q++) {
                if (q > 0) {
                    sb.append(", ");
                }
                sb.append(parameterTypes[q].toString());
            }
        }
        sb.append(')');
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<T> interfaceToImplement, final T classToWrap, final InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(classToWrap.getClass().getClassLoader(), new Class[] { interfaceToImplement }, invocationHandler);
    }

    public static List<Method> findAllGetters(final Class<?> clazz) {
        final List<Method> methods = new ArrayList<>();
        final Method[] allMethods = clazz.getMethods();
        for (final Method method : allMethods) {
            if (method.getParameterTypes().length == 0 && isGetterMethodName(method.getName())) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static boolean isGetterMethodName(final String methodName) {
        return getPropertyNameFromGetterMethodName(methodName) != null;
    }

    public static String getPropertyNameFromGetterMethodName(final String methodName) {
        final String propertyName;
        if (methodName.startsWith("get")) {
            propertyName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            propertyName = methodName.substring(2);
        } else {
            return null;
        }
        if ("Class".equals(propertyName) || propertyName.length() == 0 || !Character.isUpperCase(propertyName.charAt(0))) {
            return null;
        }
        return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
    }

}
