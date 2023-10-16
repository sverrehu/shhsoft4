package no.shhsoft.json.utils;

import no.shhsoft.json.JsonGenerator;
import no.shhsoft.json.JsonParser;
import no.shhsoft.json.impl.generator.JsonGeneratorImpl;
import no.shhsoft.json.impl.parser.JsonParserImpl;
import no.shhsoft.json.model.JsonContainer;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.UncheckedIoException;
import no.shhsoft.utils.UnknownEnumConstantException;
import no.shhsoft.web.utils.HttpAuthorization;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonHttp {

    public static final String JSON_CONTENT_TYPE = "application/json";
    private static final int DEFAULT_TIMEOUT_MS = 10 * 1000;
    private static final Logger LOG = Logger.getLogger(JsonHttp.class.getName());
    private static final JsonGenerator JSON_GENERATOR = new JsonGeneratorImpl();
    private static final JsonParser JSON_PARSER = new JsonParserImpl();

    private JsonHttp() {
    }

    private enum Method {
        GET, POST, PUT, PATCH, DELETE
    }

    public static JsonContainer get(final String url, final HttpAuthorization authorization) {
        return get(toUrl(url), authorization);
    }

    public static JsonContainer get(final URL url, final HttpAuthorization authorization) {
        return get(url, null, authorization);
    }

    public static JsonContainer get(final URL url, final Map<String, String> headerOverrides, final HttpAuthorization authorization) {
        return sendRequest(Method.GET, url, headerOverrides, authorization, null, DEFAULT_TIMEOUT_MS, true);
    }

    public static JsonContainer post(final String url, final HttpAuthorization authorization, final JsonContainer json) {
        return post(toUrl(url), authorization, json);
    }

    public static JsonContainer post(final URL url, final HttpAuthorization authorization, final JsonContainer json) {
        return post(url, null, authorization, json);
    }

    public static JsonContainer post(final URL url, final Map<String, String> headerOverrides, final HttpAuthorization authorization, final JsonContainer json) {
        return sendRequest(Method.POST, url, headerOverrides, authorization, json, DEFAULT_TIMEOUT_MS, false);
    }

    public static JsonContainer put(final String url, final HttpAuthorization authorization, final JsonContainer json) {
        return put(toUrl(url), authorization, json);
    }

    public static JsonContainer put(final URL url, final HttpAuthorization authorization, final JsonContainer json) {
        return put(url, null, authorization, json);
    }

    public static JsonContainer put(final URL url, final Map<String, String> headerOverrides, final HttpAuthorization authorization, final JsonContainer json) {
        return sendRequest(Method.PUT, url, headerOverrides, authorization, json, DEFAULT_TIMEOUT_MS, false);
    }

    public static JsonContainer patch(final String url, final HttpAuthorization authorization, final JsonContainer json) {
        return patch(toUrl(url), authorization, json);
    }

    public static JsonContainer patch(final URL url, final HttpAuthorization authorization, final JsonContainer json) {
        return patch(url, null, authorization, json);
    }

    public static JsonContainer patch(final URL url, final Map<String, String> headerOverrides, final HttpAuthorization authorization, final JsonContainer json) {
        return sendRequest(Method.PATCH, url, headerOverrides, authorization, json, DEFAULT_TIMEOUT_MS, false);
    }

    public static JsonContainer delete(final String url, final HttpAuthorization authorization) {
        return delete(toUrl(url), authorization);
    }

    public static JsonContainer delete(final URL url, final HttpAuthorization authorization) {
        return delete(url, null, authorization);
    }

    public static JsonContainer delete(final URL url, final Map<String, String> headerOverrides, final HttpAuthorization authorization) {
        return sendRequest(Method.DELETE, url, headerOverrides, authorization, null, DEFAULT_TIMEOUT_MS, false);
    }

    private static JsonContainer sendRequest(final Method method, final URL url, final Map<String, String> headerOverrides,
                                             final HttpAuthorization authorization, final JsonContainer json, final int timeoutMs, final boolean returnNullFor404) {
        final String methodString = toMethodString(method);
        try {
            final URLConnection tmpConn = url.openConnection();
            if (!(tmpConn instanceof HttpURLConnection)) {
                throw new RuntimeException("Attempt to " + methodString + " using other scheme than HTTP");
            }
            final HttpURLConnection conn = (HttpURLConnection) tmpConn;
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            if (method == Method.PATCH) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            } else {
                conn.setRequestMethod(methodString);
            }
            if (authorization != null) {
                conn.setRequestProperty("Authorization", authorization.getValue());
            }
            if (json != null) {
                conn.setRequestProperty("Content-Type", JSON_CONTENT_TYPE);
            }
            if (headerOverrides != null) {
                for (final Map.Entry<String, String> keyValue : headerOverrides.entrySet()) {
                    conn.setRequestProperty(keyValue.getKey(), keyValue.getValue());
                }
            }
            conn.setDoInput(true);
            if (json != null) {
                conn.setDoOutput(true);
                final BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
                out.write(StringUtils.getBytesUtf8(JSON_GENERATOR.generate(json)));
                out.flush();
                out.close();
            } else {
                conn.setDoOutput(false);
            }
            final int responseCode = conn.getResponseCode();
            if (responseCode == 404 && returnNullFor404) {
                return null;
            }
            if (responseCode < 200 || responseCode >= 300) {
                final String result = StringUtils.newStringUtf8(IoUtils.read(conn.getErrorStream()));
                throw new UncheckedIoException("Response code: " + responseCode + ". Expected 200. Response:\n" + result);
            }
            final String result = StringUtils.newStringUtf8(IoUtils.read(conn.getInputStream()));
            return StringUtils.isBlank(result) ? null : JSON_PARSER.parse(result);
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    private static String toMethodString(final Method method) {
        switch (method) {
            case GET:
                return "GET";
            case POST:
                return "POST";
            case PUT:
                return "PUT";
            case PATCH:
                return "PATCH";
            case DELETE:
                return "DELETE";
            default:
                throw new UnknownEnumConstantException(method);
        }
    }

    private static URL toUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
    }

}
