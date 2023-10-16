package no.shhsoft.validation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UrlValidationUtilsTest
extends TestSuite {

    void thisClassIsNotAUtilityClass() {
        /* just to silence CheckStyle that wants me to add a private
         * constructor that makes Surefire backfire. */
    }

    private static String createLongString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("/foo");
        for (int q = 0; q < 2000; q++) {
            sb.append('f');
        }
        return sb.toString();
    }

    public static Test suite() {
        final TestSuite suite = new TestSuite(UrlValidationUtilsTest.class.getName());

        suite.addTest(validUrlTest("/"));
        suite.addTest(validUrlTest("/foo"));
        suite.addTest(validUrlTest("/foo/bar"));
        suite.addTest(validUrlTest("/foo/bar.jsp"));
        suite.addTest(validUrlTest("/foo/bar.jsp/something"));

        suite.addTest(invalidUrlTest(""));
        suite.addTest(invalidUrlTest("foo"));
        suite.addTest(invalidUrlTest("/foo..bar"));
        suite.addTest(invalidUrlTest("/foo/.hidden"));
        suite.addTest(invalidUrlTest("/foo./bar"));
        suite.addTest(invalidUrlTest("/foo//bar"));
        suite.addTest(invalidUrlTest("/foo%41"));
        suite.addTest(invalidUrlTest("/foo\\\\bar", "/foo\\bar"));
        suite.addTest(invalidUrlTest("/foo\\nbar", "/foo\nbar"));
        suite.addTest(invalidUrlTest("/foo:bar"));
        suite.addTest(invalidUrlTest("/foo\\000", "/foo\000"));
        suite.addTest(invalidUrlTest("/foo?bar"));
        suite.addTest(invalidUrlTest("/foo/../bar"));
        suite.addTest(invalidUrlTest("foo&bar"));
        suite.addTest(invalidUrlTest("really long url", createLongString()));

        return suite;
    }

    private static Test validUrlTest(final String url) {
        return new TestCase("validUrlTest \"" + url + "\"") {
            @Override
            public void runBare() throws Throwable {
                assertTrue("URL path <" + url + "> should be valid",
                           UrlValidationUtils.isPathOk(url));
            }
        };
    }

    private static Test invalidUrlTest(final String url) {
        return invalidUrlTest(url, url);
    }

    private static Test invalidUrlTest(final String description, final String url) {
        return new TestCase("invalidUrlTest \"" + description + "\"") {
            @Override
            public void runBare() throws Throwable {
                assertFalse("Path " + url + " should not validate",
                            UrlValidationUtils.isPathOk(url));
            }
        };
    }

}
