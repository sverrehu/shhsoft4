package no.shhsoft.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringConversionUtils {

    private static final Map<Class<?>, StringConverter<?>> CONVERTERS;

    static {
        CONVERTERS = new HashMap<>();
        setConverter(String.class, new StringConverter<String>() {
            @Override
            public boolean isValid(final String s) {
                return s != null;
            }

            @Override
            public String convertFromString(final String s) {
                return s;
            }

            @Override
            public String convertToString(final String t) {
                return t;
            }
        });
        setConverter(Integer.class, Integer.TYPE, new StringConverter<Integer>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Integer convertFromString(final String s) {
                return Integer.valueOf(s);
            }

            @Override
            public String convertToString(final Integer t) {
                return String.valueOf(t);
            }
        });
        setConverter(Long.class, Long.TYPE, new StringConverter<Long>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Long convertFromString(final String s) {
                return Long.valueOf(s);
            }

            @Override
            public String convertToString(final Long t) {
                return String.valueOf(t);
            }
        });
        setConverter(Short.class, Short.TYPE, new StringConverter<Short>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Short convertFromString(final String s) {
                return Short.valueOf(s);
            }

            @Override
            public String convertToString(final Short t) {
                return String.valueOf(t);
            }
        });
        setConverter(Byte.class, Byte.TYPE, new StringConverter<Byte>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Byte convertFromString(final String s) {
                return Byte.valueOf(s);
            }

            @Override
            public String convertToString(final Byte t) {
                return String.valueOf(t);
            }
        });
        setConverter(Boolean.class, Boolean.TYPE, new StringConverter<Boolean>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Boolean convertFromString(final String s) {
                return Boolean.valueOf(s);
            }

            @Override
            public String convertToString(final Boolean t) {
                return String.valueOf(t);
            }
        });
        setConverter(Double.class, Double.TYPE, new StringConverter<Double>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Double convertFromString(final String s) {
                return Double.valueOf(s);
            }

            @Override
            public String convertToString(final Double t) {
                return String.valueOf(t);
            }
        });
        setConverter(Float.class, Float.TYPE, new StringConverter<Float>() {
            @Override
            public boolean isValid(final String s) {
                try {
                    convertFromString(s);
                    return true;
                } catch (final Exception e) {
                    return false;
                }
            }

            @Override
            public Float convertFromString(final String s) {
                return Float.valueOf(s);
            }

            @Override
            public String convertToString(final Float t) {
                return String.valueOf(t);
            }
        });
    }

    private StringConversionUtils() {
    }

    private static StringConverter<?> findConverter(final Class<?> convertToClazz) {
        final StringConverter<?> converter = CONVERTERS.get(convertToClazz);
        if (converter == null) {
            throw new StringConversionException(
                "No string converters available for type `" + convertToClazz.getName() + "'.");
        }
        return converter;
    }

    public static void setConverter(final Class<?> clazz, final StringConverter<?> converter) {
        CONVERTERS.put(clazz, converter);
    }

    private static void setConverter(final Class<?> clazz1, final Class<?> clazz2,
                                     final StringConverter<?> converter) {
        setConverter(clazz1, converter);
        setConverter(clazz2, converter);
    }

    public static boolean isConvertibleFromString(final String s, final Class<?> convertToClazz) {
        final StringConverter<?> converter = findConverter(convertToClazz);
        return converter.isValid(s);
    }

    public static Object convertFromString(final String s, final Class<?> convertToClazz) {
        final StringConverter<?> converter = findConverter(convertToClazz);
        if (!converter.isValid(s)) {
            throw new StringConversionException("String `" + s + "' is not convertible to `"
                                                + convertToClazz.getName() + "'.");
        }
        try {
            return converter.convertFromString(s);
        } catch (final Exception e) {
            throw new StringConversionException(
                "Error converting string `" + s + "' to an object of type `"
                + convertToClazz.getName() + "'.", e);
        }
    }

    public static <T> String convertToString(final T o) {
        final Class<?> clazz = o.getClass();
        @SuppressWarnings("unchecked")
        final StringConverter<T> converter = (StringConverter<T>) findConverter(clazz);
        try {
            return converter.convertToString(o);
        } catch (final Exception e) {
            throw new StringConversionException(
                "Error converting an object of type `" + clazz.getName() + "' to a string.", e);
        }
    }

}
