package no.shhsoft.web.utils;

import no.shhsoft.utils.Base64Utils;
import no.shhsoft.utils.StringUtils;
import no.shhsoft.validation.Validate;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HttpAuthorization {

    private final String value;

    private HttpAuthorization(final String value) {
        this.value = value;
    }

    public static HttpAuthorization forBasicAuth(final String userName, final String password) {
        Validate.notNull(userName);
        Validate.isTrue(!userName.contains(":"), "For Basic auth, the user name cannot contain a colon character");
        Validate.notNull(password);
        final String credentials = userName + ":" + password;
        final String encodedCredentials = Base64Utils.encode(StringUtils.getBytesUtf8(credentials));
        return new HttpAuthorization("Basic " + encodedCredentials);
    }

    public static HttpAuthorization forOauthBearerToken(final String token) {
        Validate.notNull(token);
        return new HttpAuthorization("Bearer " + token);
    }

    public String getValue() {
        return value;
    }

}
