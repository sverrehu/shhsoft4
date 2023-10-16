package no.shhsoft.web.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UrlUtilsTest
extends TestSuite {

    private static final String PATH_INFO = "/some/path";
    private static final String QUERY_STRING = "foo=bar&xyzzy=gazonk";
    private static final String PATH = PATH_INFO + "?" + QUERY_STRING;
    private static final String HOST = "www.example";

    void thisClassIsNotAUtilityClass() {
        /* just to silence CheckStyle that wants me to add a private
         * constructor that makes Surefire backfire. */
    }

    public static Test suite() {
        final TestSuite suite = new TestSuite(UrlUtilsTest.class.getName());

        suite.addTest(extractQueryPathTest("http://" + HOST + PATH, PATH));
        suite.addTest(extractQueryPathTest("http://foo:bar@" + HOST + PATH, PATH));
        suite.addTest(extractQueryPathTest(PATH, PATH));
        suite.addTest(extractQueryPathTest("http://" + HOST + "/", "/"));
        suite.addTest(extractQueryPathTest("http://" + HOST, "/"));
        suite.addTest(extractQueryPathTest("foo", "foo"));
        suite.addTest(extractQueryPathTest(PATH + "&://foo", PATH + "&://foo"));
        suite.addTest(extractQueryPathTest("xx/xx" + PATH, "xx/xx" + PATH));

        suite.addTest(extractPathTest("http://" + HOST + PATH, PATH_INFO));
        suite.addTest(extractPathTest("http://" + HOST + PATH_INFO, PATH_INFO));
        suite.addTest(extractPathTest(PATH, PATH_INFO));
        suite.addTest(extractPathTest(PATH_INFO, PATH_INFO));

        suite.addTest(extractHostTest("http://" + HOST, HOST));
        suite.addTest(extractHostTest("http://" + HOST + ":8080", HOST));
        suite.addTest(extractHostTest("http://" + HOST + PATH, HOST));
        suite.addTest(extractHostTest("http://" + HOST + ":8080" + PATH, HOST));
        suite.addTest(extractHostTest("http://user@" + HOST + PATH, HOST));
        suite.addTest(extractHostTest("http://user:password@" + HOST + PATH, HOST));
        suite.addTest(extractHostTest("http://user:password@" + HOST + ":8080" + PATH, HOST));
        suite.addTest(extractHostTest("https://user:password@" + HOST + ":8080" + PATH, HOST));
        suite.addTest(extractHostTest("file:///foo/bar", null));
        suite.addTest(extractHostTest("file:/foo/bar", null));
        suite.addTest(extractHostTest("file://" + HOST + "/foo/bar", HOST));
        suite.addTest(extractHostTest(PATH, null));

        return suite;
    }

    private static Test extractQueryPathTest(final String url,
                                             final String queryPath) {
        return new TestCase("extractQueryPathTest \"" + url + "\" -> \"" + queryPath + "\"") {
            @Override
            public void runBare() throws Throwable {
                assertEquals("QueryPath of " + url, queryPath,
                             UrlUtils.extractPathAndQueryString(url));
            }
        };
    }

    private static Test extractPathTest(final String url, final String queryPath) {
        return new TestCase("extractPathTest \"" + url + "\" -> \"" + queryPath + "\"") {
            @Override
            public void runBare() throws Throwable {
                assertEquals("Path of " + url, queryPath, UrlUtils.extractPath(url));
            }
        };
    }

    private static Test extractHostTest(final String url, final String host) {
        return new TestCase("extractHostTest \"" + url + "\" -> \"" + host + "\"") {
            @Override
            public void runBare() throws Throwable {
                assertEquals("host of " + url, host, UrlUtils.extractHost(url));
            }
        };
    }

}
