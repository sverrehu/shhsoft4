package no.shhsoft.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class OptionParser {

    private boolean allowNegativeNumbers = false;
    private Opt[] opts;

    private static void fatal(final String msg) {
        throw new RuntimeException(msg);
    }

    private static Object convert(final String s, final Class<?> type) {
        if (type == null) {
            return null;
        }
        if (type == String.class) {
            return s;
        }
        try {
            if (type == Byte.TYPE || type == Byte.class) {
                return new Byte(s);
            }
            if (type == Short.TYPE || type == Short.class) {
                return new Short(s);
            }
            if (type == Integer.TYPE || type == Integer.class) {
                return new Integer(s);
            }
            if (type == Long.TYPE || type == Long.class) {
                return new Long(s);
            }
            if (type == Float.TYPE || type == Float.class) {
                return new Float(s);
            }
            if (type == Double.TYPE || type == Double.class) {
                return new Double(s);
            }
            if (type == Boolean.TYPE || type == Boolean.class) {
                if ("1".equals(s) || "t".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s)
                    || "y".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s)) {
                    return Boolean.TRUE;
                }
                if ("0".equals(s) || "f".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)
                    || "n".equalsIgnoreCase(s) || "no".equalsIgnoreCase(s)) {
                    return Boolean.FALSE;
                }
                fatal("`" + s + "' is not a valid boolean indicator.");
            }
        } catch (final NumberFormatException e) {
            fatal("cannot convert `" + s + "' to the required type `" + type.getName() + "'.");
        }
        fatal("unsupported parameter type `" + type.getName() + "'.");
        return null; /* to please the compiler */
    }

    private Opt findShortOpt(final char name) {
        for (final Opt opt : opts) {
            if (name == opt.getShortName()) {
                return opt;
            }
        }
        return null;
    }

    private Opt findLongOpt(final String name) {
        for (final Opt opt : opts) {
            if (name.equals(opt.getLongName())) {
                return opt;
            }
        }
        return null;
    }

    @SuppressWarnings("null")
    private static void doProperty(final Opt opt, final String value, final Object optionHandler) {
        Class<?>[] typeSpec = null;
        if (opt.takesValue()) {
            typeSpec = new Class<?>[] { opt.getType() };
        }
        Method method = null;
        try {
            method = optionHandler.getClass().getMethod(opt.getProperty(), typeSpec);
        } catch (final NoSuchMethodException e) {
            fatal("unable to find method `" + opt.getProperty() + "'");
        }
        Object[] params = null;
        if (opt.takesValue()) {
            params = new Object[] { convert(value, opt.getType()) };
        }
        try {
            method.invoke(optionHandler, params);
        } catch (final Throwable t) {
            fatal("unable to call method `" + opt.getProperty() + "': " + t.getMessage());
        }
    }

    private static void doOpt(final Opt opt, final String calledAs, final String value,
                              final List<String> list, final int idx, final Object optionHandler) {
        if (!opt.takesValue() && value != null) {
            fatal("option `" + calledAs + "' doesn't take a value.");
        }
        String theValue = value;
        if (opt.takesValue() && theValue == null) {
            if (idx == list.size() - 1) {
                fatal("option `" + calledAs + "' requires a value.");
            }
            theValue = list.remove(idx + 1);
        }
        doProperty(opt, theValue, optionHandler);
    }

    @SuppressWarnings("null")
    private void parseShortOpts(final List<String> list, final int idx, final Object optionHandler) {
        String arg = list.get(idx);
        if (arg.startsWith("-")) {
            arg = arg.substring(1);
        }
        while (arg.length() > 0) {
            final char c = arg.charAt(0);
            final Opt opt = findShortOpt(c);
            if (opt == null) {
                fatal("unrecognized option `-" + c + "'");
            }
            String value = null;
            if (opt.takesValue() && arg.length() > 1) {
                value = arg.substring(1);
                arg = "";
            } else {
                arg = arg.substring(1);
            }
            doOpt(opt, "-" + c, value, list, idx, optionHandler);
        }
    }

    private void parseLongOpt(final List<String> list, final int idx, final Object optionHandler) {
        String arg = list.get(idx);
        if (arg.startsWith("--")) {
            arg = arg.substring(2);
        }
        String value = null;
        final int i = arg.indexOf('=');
        if (i >= 0) {
            value = arg.substring(i + 1);
            arg = arg.substring(0, i);
        }
        final Opt opt = findLongOpt(arg);
        if (opt == null) {
            fatal("unrecognized option `--" + arg + "'");
        }
        doOpt(opt, "--" + arg, value, list, idx, optionHandler);
    }

    /** */
    public static final class Opt {

        private char shortName;
        private String longName;
        private String property;
        private Class<?> type;

        public Opt() {
        }

        public Opt(final char shortName, final String longName, final String property) {
            setShortName(shortName);
            setLongName(longName);
            setProperty(property);
            setType(null);
        }

        public Opt(final char shortName, final String longName, final String property,
                   final Class<?> type) {
            setShortName(shortName);
            setLongName(longName);
            setProperty(property);
            setType(type);
        }

        public char getShortName() {
            return shortName;
        }

        public void setShortName(final char shortName) {
            this.shortName = shortName;
        }

        public String getLongName() {
            return longName;
        }

        public void setLongName(final String longName) {
            this.longName = longName;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(final String property) {
            this.property = property;
        }

        public void setType(final Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }

        public boolean takesValue() {
            return type != null;
        }

    }

    public OptionParser() {
    }

    public OptionParser(final Opt[] opts) {
        setOpts(opts);
    }

    public void setAllowNegativeNumbers(final boolean allowNegativeNumbers) {
        this.allowNegativeNumbers = allowNegativeNumbers;
    }

    public void setOpts(final Opt[] opts) {
        this.opts = opts;
    }

    public String[] parseWithException(final String[] args, final Object optHandler) {
        final List<String> list = new ArrayList<>(Arrays.asList(args));
        int idx = 0;
        while (idx < list.size()) {
            final String arg = list.get(idx);
            if (arg.startsWith("--")) {
                if (arg.length() == 2) {
                    list.remove(idx);
                    break;
                }
                parseLongOpt(list, idx, optHandler);
                list.remove(idx);
            } else if (arg.startsWith("-")) {
                if (arg.length() == 1) {
                    /* a dash by itself is not considered an option. */
                    ++idx;
                    continue;
                }
                if (allowNegativeNumbers && Character.isDigit(arg.charAt(1))) {
                    ++idx;
                    continue;
                }
                parseShortOpts(list, idx, optHandler);
                list.remove(idx);
            } else {
                ++idx;
            }
        }
        return list.toArray(new String[0]);
    }

    public String[] parse(final String[] args, final Object optHandler) {
        try {
            return parseWithException(args, optHandler);
        } catch (final RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return null; /* to please the compiler */
        }
    }

}
