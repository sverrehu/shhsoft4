package no.shhsoft.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RfcHeaders {

    private final Map<CaseInsensitiveKey, List<String>> map = new LinkedHashMap<>();

    private static final class CaseInsensitiveKey {

        private final String key;
        private final String lowerCaseKey;

        CaseInsensitiveKey(final String key) {
            this.key = key;
            this.lowerCaseKey = key.toLowerCase();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return lowerCaseKey.equals(((CaseInsensitiveKey) o).lowerCaseKey);
        }

        @Override
        public int hashCode() {
            return lowerCaseKey.hashCode();
        }

    }

    public void put(final String keyString, final String value) {
        synchronized (map) {
            final CaseInsensitiveKey key = new CaseInsensitiveKey(keyString);
            final List<String> values = map.computeIfAbsent(key, k -> new ArrayList<>());
            values.add(value);
        }
    }

    public List<String> get(final String keyString) {
        synchronized (map) {
            final List<String> values = map.get(new CaseInsensitiveKey(keyString));
            return values != null ? values : Collections.emptyList();
        }
    }

    public String getSingle(final String keyString) {
        final List<String> values = get(keyString);
        return values.isEmpty() ? null : values.get(0);
    }

    public String toHeaderLines() {
        final StringBuilder sb = new StringBuilder();
        synchronized (map) {
            for (final Map.Entry<CaseInsensitiveKey, List<String>> entry : map.entrySet()) {
                for (final String value : entry.getValue()) {
                    sb.append(entry.getKey().key);
                    sb.append(':');
                    sb.append(value);
                    sb.append("\r\n");
                }
            }
        }
        sb.append("\r\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toHeaderLines();
    }

    public static RfcHeaders parse(final String s) {
        return parse(StringUtils.getBytesUtf8(s));
    }

    public static RfcHeaders parse(final byte[] b) {
        return parse(new ByteArrayInputStream(b));
    }

    /*
     * Reads up to and including the empty line after the headers.
     */
    public static RfcHeaders parse(final InputStream in) {
        final RfcHeaders headers = new RfcHeaders();
        String nextLine = null;
        for (;;) {
            String line = nextLine != null ? nextLine : readLine(in);
            if (line == null || line.isEmpty()) {
                break;
            }
            for (;;) {
                nextLine = readLine(in);
                if (nextLine != null && !nextLine.isEmpty() && Character.isWhitespace(nextLine.charAt(0))) {
                    line += nextLine;
                } else {
                    break;
                }
            }
            final int pos = line.indexOf(':');
            if (pos < 0) {
                throw new RuntimeException("Missing ':' in header line \"" + line + "\"");
            }
            final String key = line.substring(0, pos);
            final String value = line.substring(pos + 1);
            headers.put(key, value);
        }
        return headers;
    }

    public static String readLine(final InputStream in) {
        try {
            final StringBuilder sb = new StringBuilder();
            for (;;) {
                final int c = in.read();
                if (c == -1) {
                    if (sb.length() == 0) {
                        return null;
                    }
                    break;
                }
                if (c == '\n') {
                    break;
                }
                if (c != '\r') {
                    sb.append((char) c);
                }
            }
            return sb.toString();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

}
