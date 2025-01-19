package no.shhsoft.web.utils;

import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.UncheckedIoException;
import no.shhsoft.utils.UnknownEnumConstantException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HttpFetcher2 {

    private static final Logger LOG = Logger.getLogger(HttpFetcher2.class.getName());
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final On201 on201;
    private final String on201JsonLocationName;

    public enum On201 {
        RETURN_LOCATION_AS_STRING,
        RETURN_LOCATION_AS_JSON
    }

    public HttpFetcher2() {
        this(null, null);
    }

    public HttpFetcher2(final On201 on201) {
        this(on201, "location");
    }

    public HttpFetcher2(final On201 on201, final String on201JsonLocationName) {
        this.on201 = on201;
        this.on201JsonLocationName = on201JsonLocationName;
    }

    private enum Method {
        GET, POST, PUT
    }

    public String getOn201JsonLocationName() {
        return on201JsonLocationName;
    }

    public byte[] postWithRetries(final String url, final Map<String, String> keyValues, final int numRetries, final int timeoutMs) {
        return postWithRetries(toUrl(url), keyValues, numRetries, timeoutMs);
    }

    public byte[] postWithRetries(final URL url, final Map<String, String> keyValues, final int numRetries, final int timeoutMs) {
        for (int retry = 0; retry <= numRetries; retry++) {
            try {
                return post(url, keyValues, timeoutMs);
            } catch (final UncheckedIoException e) {
                if (retry == numRetries) {
                    throw e;
                }
                LOG.log(Level.WARNING, "Post of SMS failed. Retrying...", e);
            }
        }
        throw new RuntimeException("Should never reach this code.");
    }

    public byte[] post(final String url) {
        return post(toUrl(url));
    }

    public byte[] post(final String url, final Map<String, String> keyValues) {
        return post(toUrl(url), keyValues);
    }

    public byte[] post(final String url, final String contentType, final Map<String, String> keyValues) {
        return post(toUrl(url), contentType, keyValues);
    }

    public byte[] post(final String url, final Map<String, String> keyValues, final int timeoutMs) {
        return post(toUrl(url), keyValues, timeoutMs);
    }

    public byte[] post(final String url, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return post(toUrl(url), contentType, keyValues, timeoutMs);
    }

    public byte[] post(final String url, final String requestBody) {
        return post(toUrl(url), requestBody);
    }

    public byte[] post(final String url, final String contentType, final String requestBody) {
        return post(toUrl(url), contentType, requestBody);
    }

    public byte[] post(final String url, final String requestBody, final int timeoutMs) {
        return post(toUrl(url), requestBody, timeoutMs);
    }

    public byte[] post(final String url, final String contentType, final String requestBody, final int timeoutMs) {
        return post(toUrl(url), contentType, requestBody, timeoutMs);
    }

    public byte[] post(final URL url) {
        return post(url, (Map<String, String>) null, 0);
    }

    public byte[] post(final URL url, final Map<String, String> keyValues) {
        return post(url, keyValues, 0);
    }

    public byte[] post(final URL url, final String contentType, final Map<String, String> keyValues) {
        return post(url, contentType, keyValues, 0);
    }

    public byte[] post(final URL url, final Map<String, String> keyValues, final int timeoutMs) {
        return post(url, (String) null, keyValues, timeoutMs);
    }

    public byte[] post(final URL url, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return request(url, null, contentType, keyValues, null, timeoutMs, Method.POST);
    }

    public byte[] post(final URL url, final String requestBody) {
        return post(url, requestBody, 0);
    }

    public byte[] post(final URL url, final String contentType, final String requestBody) {
        return post(url, contentType, requestBody, 0);
    }

    public byte[] post(final URL url, final String requestBody, final int timeoutMs) {
        return post(url, (String) null, requestBody, timeoutMs);
    }

    public byte[] post(final URL url, final String contentType, final String requestBody, final int timeoutMs) {
        return request(url, null, contentType, null, requestBody, timeoutMs, Method.POST);
    }

    public byte[] post(final String url, final HttpAuthorization authorization) {
        return post(toUrl(url), authorization);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final Map<String, String> keyValues) {
        return post(toUrl(url), authorization, keyValues);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues) {
        return post(toUrl(url), authorization, contentType, keyValues);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final Map<String, String> keyValues, final int timeoutMs) {
        return post(toUrl(url), authorization, keyValues, timeoutMs);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return post(toUrl(url), authorization, contentType, keyValues, timeoutMs);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final String requestBody) {
        return post(toUrl(url), authorization, requestBody);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final String contentType, final String requestBody) {
        return post(toUrl(url), authorization, contentType, requestBody);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final String requestBody, final int timeoutMs) {
        return post(toUrl(url), authorization, requestBody, timeoutMs);
    }

    public byte[] post(final String url, final HttpAuthorization authorization, final String contentType, final String requestBody, final int timeoutMs) {
        return post(toUrl(url), authorization, contentType, requestBody, timeoutMs);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization) {
        return post(url, authorization, (Map<String, String>) null, 0);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final Map<String, String> keyValues) {
        return post(url, authorization, keyValues, 0);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues) {
        return post(url, authorization, contentType, keyValues, 0);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final Map<String, String> keyValues, final int timeoutMs) {
        return post(url, authorization, null, keyValues, timeoutMs);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return request(url, authorization, contentType, keyValues, null, timeoutMs, Method.POST);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final String requestBody) {
        return post(url, authorization, requestBody, 0);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final String contentType, final String requestBody) {
        return post(url, authorization, contentType, requestBody, 0);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final String requestBody, final int timeoutMs) {
        return post(url, authorization, null, requestBody, timeoutMs);
    }

    public byte[] post(final URL url, final HttpAuthorization authorization, final String contentType, final String requestBody, final int timeoutMs) {
        return request(url, authorization, contentType, null, requestBody, timeoutMs, Method.POST);
    }

    public byte[] get(final String url) {
        return get(toUrl(url));
    }

    public byte[] get(final String url, final Map<String, String> keyValues) {
        return get(toUrl(url), keyValues);
    }

    public byte[] get(final String url, final Map<String, String> keyValues, final int timeoutMs) {
        return get(toUrl(url), keyValues, timeoutMs);
    }

    public byte[] get(final URL url) {
        return get(url, null, 0);
    }

    public byte[] get(final URL url, final Map<String, String> keyValues) {
        return get(url, keyValues, 0);
    }

    public byte[] get(final URL url, final Map<String, String> keyValues, final int timeoutMs) {
        return get(url, (String) null, keyValues, timeoutMs);
    }

    public byte[] get(final URL url, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return request(url, null, contentType, keyValues, null, timeoutMs, Method.GET);
    }

    public byte[] get(final String url, final HttpAuthorization authorization) {
        return get(toUrl(url), authorization);
    }

    public byte[] get(final String url, final HttpAuthorization authorization, final Map<String, String> keyValues) {
        return get(toUrl(url), authorization, keyValues);
    }

    public byte[] get(final String url, final HttpAuthorization authorization, final Map<String, String> keyValues, final int timeoutMs) {
        return get(toUrl(url), authorization, keyValues, timeoutMs);
    }

    public byte[] get(final URL url, final HttpAuthorization authorization) {
        return get(url, authorization, null, 0);
    }

    public byte[] get(final URL url, final HttpAuthorization authorization, final Map<String, String> keyValues) {
        return get(url, authorization, keyValues, 0);
    }

    public byte[] get(final URL url, final HttpAuthorization authorization, final Map<String, String> keyValues, final int timeoutMs) {
        return get(url, authorization, null, keyValues, timeoutMs);
    }

    public byte[] get(final URL url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return request(url, authorization, contentType, keyValues, null, timeoutMs, Method.GET);
    }

    public byte[] put(final String url) {
        return put(toUrl(url));
    }

    public byte[] put(final String url, final Map<String, String> keyValues) {
        return put(toUrl(url), keyValues);
    }

    public byte[] put(final String url, final String contentType, final Map<String, String> keyValues) {
        return put(toUrl(url), contentType, keyValues);
    }

    public byte[] put(final String url, final Map<String, String> keyValues, final int timeoutMs) {
        return put(toUrl(url), keyValues, timeoutMs);
    }

    public byte[] put(final String url, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return put(toUrl(url), contentType, keyValues, timeoutMs);
    }

    public byte[] put(final String url, final String requestBody) {
        return put(toUrl(url), requestBody);
    }

    public byte[] put(final String url, final String contentType, final String requestBody) {
        return put(toUrl(url), contentType, requestBody);
    }

    public byte[] put(final String url, final String requestBody, final int timeoutMs) {
        return put(toUrl(url), requestBody, timeoutMs);
    }

    public byte[] put(final String url, final String contentType, final String requestBody, final int timeoutMs) {
        return put(toUrl(url), contentType, requestBody, timeoutMs);
    }

    public byte[] put(final URL url) {
        return put(url, (Map<String, String>) null, 0);
    }

    public byte[] put(final URL url, final Map<String, String> keyValues) {
        return put(url, keyValues, 0);
    }

    public byte[] put(final URL url, final String contentType, final Map<String, String> keyValues) {
        return put(url, contentType, keyValues, 0);
    }

    public byte[] put(final URL url, final Map<String, String> keyValues, final int timeoutMs) {
        return put(url, (String) null, keyValues, timeoutMs);
    }

    public byte[] put(final URL url, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return request(url, null, contentType, keyValues, null, timeoutMs, Method.PUT);
    }

    public byte[] put(final URL url, final String requestBody) {
        return put(url, requestBody, 0);
    }

    public byte[] put(final URL url, final String contentType, final String requestBody) {
        return put(url, contentType, requestBody, 0);
    }

    public byte[] put(final URL url, final String requestBody, final int timeoutMs) {
        return put(url, null, null, requestBody, timeoutMs);
    }

    public byte[] put(final URL url, final String contentType, final String requestBody, final int timeoutMs) {
        return request(url, null, contentType, null, requestBody, timeoutMs, Method.PUT);
    }

    public byte[] put(final String url, final HttpAuthorization authorization) {
        return put(toUrl(url), authorization);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final Map<String, String> keyValues) {
        return put(toUrl(url), authorization, keyValues);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues) {
        return put(toUrl(url), authorization, contentType, keyValues);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final Map<String, String> keyValues, final int timeoutMs) {
        return put(toUrl(url), authorization, keyValues, timeoutMs);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return put(toUrl(url), authorization, contentType, keyValues, timeoutMs);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final String requestBody) {
        return put(toUrl(url), authorization, requestBody);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final String contentType, final String requestBody) {
        return put(toUrl(url), authorization, contentType, requestBody);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final String requestBody, final int timeoutMs) {
        return put(toUrl(url), authorization, requestBody, timeoutMs);
    }

    public byte[] put(final String url, final HttpAuthorization authorization, final String contentType, final String requestBody, final int timeoutMs) {
        return put(toUrl(url), authorization, contentType, requestBody, timeoutMs);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization) {
        return put(url, authorization, (Map<String, String>) null, 0);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final Map<String, String> keyValues) {
        return put(url, authorization, keyValues, 0);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues) {
        return put(url, authorization, contentType, keyValues, 0);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final Map<String, String> keyValues, final int timeoutMs) {
        return put(url, authorization, null, keyValues, timeoutMs);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues, final int timeoutMs) {
        return request(url, authorization, contentType, keyValues, null, timeoutMs, Method.PUT);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final String requestBody) {
        return put(url, authorization, requestBody, 0);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final String contentType, final String requestBody) {
        return put(url, authorization, contentType, requestBody, 0);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final String requestBody, final int timeoutMs) {
        return put(url, authorization, null, requestBody, timeoutMs);
    }

    public byte[] put(final URL url, final HttpAuthorization authorization, final String contentType, final String requestBody, final int timeoutMs) {
        return request(url, authorization, contentType, null, requestBody, timeoutMs, Method.PUT);
    }

    private byte[] request(final URL url, final HttpAuthorization authorization, final String contentType, final Map<String, String> keyValues, final String requestBody, final int timeoutMs, final Method method) {
        if (requestBody != null && keyValues != null) {
            throw new RuntimeException("Cannot have both request body and key/value map");
        }
        final String wwwUrlEncoded = toWwwUrlEncoded(keyValues);
        final String requestMethod = toMethodString(method);
        final boolean parametersInUrl = isMethodWithParametersInUrl(method);
        final URL actualUrl = parametersInUrl ? addParams(url, wwwUrlEncoded) : url;
        try {
            final URLConnection tmpConn = actualUrl.openConnection();
            if (!(tmpConn instanceof HttpURLConnection)) {
                throw new RuntimeException("Attempt to " + requestMethod + " using other scheme than HTTP");
            }
            final HttpURLConnection conn = (HttpURLConnection) tmpConn;
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setRequestMethod(requestMethod);
            if (authorization != null) {
                conn.setRequestProperty("Authorization", authorization.getValue());
            }
            conn.setDoInput(true);
            if (wwwUrlEncoded != null && !parametersInUrl) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", contentType == null ? DEFAULT_CONTENT_TYPE : contentType);
                final BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
                out.write(StringUtils.getBytesUtf8(wwwUrlEncoded));
                out.flush();
                out.close();
            } else if (requestBody != null) {
                conn.setDoOutput(true);
                if (contentType != null) {
                    conn.setRequestProperty("Content-Type", contentType);
                }
                final BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
                out.write(StringUtils.getBytesUtf8(requestBody));
                out.flush();
                out.close();
            } else {
                conn.setDoOutput(false);
            }
            final int responseCode = conn.getResponseCode();
            if (responseCode >= 300) {
                final InputStream errorStream = conn.getErrorStream();
                final byte[] messageBody = errorStream != null ? IoUtils.read(errorStream) : null;
                throw new UnexpectedHttpStatusCodeException(responseCode, messageBody, "Expected 2xx.");
            }
            if (responseCode == 201 && on201 != null) {
                final String location = conn.getHeaderField("Location");
                switch (on201) {
                    case RETURN_LOCATION_AS_STRING:
                        return StringUtils.getBytesUtf8(location);
                    case RETURN_LOCATION_AS_JSON:
                        return StringUtils.getBytesUtf8("{\"" + JavaScriptUtils.escapeString(on201JsonLocationName)
                                                        + "\":\"" + JavaScriptUtils.escapeString(location) + "\"}");
                    default:
                        throw new UnknownEnumConstantException(on201);
                }
            }
            return IoUtils.read(conn.getInputStream());
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
            default:
                throw new UnknownEnumConstantException(method);
        }
    }

    private static boolean isMethodWithParametersInUrl(final Method method) {
        return method == Method.GET;
    }

    public static URL toUrl(final String initialUrl, final Map<String, String> keyValues) {
        return toUrl(toUrl(initialUrl), keyValues);
    }

    public static URL toUrl(final URL initialUrl, final Map<String, String> keyValues) {
        return addParams(initialUrl, toWwwUrlEncoded(keyValues));
    }

    private static String toWwwUrlEncoded(final Map<String, String> keyValues) {
        if (keyValues == null || keyValues.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, String> keyValue : keyValues.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(UrlUtils.encode(keyValue.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(UrlUtils.encode(keyValue.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    private static URL toUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
    }

    static URL addParams(final URL initialUrl, final String encodedParams) {
        try {
            String fileName = initialUrl.getFile();
            if (!StringUtils.isEmpty(encodedParams)) {
                fileName += (fileName.contains("?") ? "&" : "?") + encodedParams;
            }
            final String ref = initialUrl.getRef();
            if (!StringUtils.isBlank(ref)) {
                fileName += "#" + ref;
            }
            return new URL(initialUrl.getProtocol(), initialUrl.getHost(), initialUrl.getPort(), fileName);
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
    }

}
