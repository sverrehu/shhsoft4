package no.shhsoft.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class MultiTrustStoreX509TrustManagerTest {

    private static X509Certificate signedCertificate;
    private static MultiTrustStoreX509TrustManager defaultTrustManager;
    private static MultiTrustStoreX509TrustManager trustManagerWithCa;

    @BeforeAll
    public static void beforeAll() {
        signedCertificate = loadSingleCertificate("/truststore-certs/signed-cert.pem");
        defaultTrustManager = MultiTrustStoreX509TrustManager.withDefaultTrustStore();
        trustManagerWithCa = MultiTrustStoreX509TrustManager.withDefaultTrustStore().withCaCertificateResource("/truststore-certs/ca.pem");
    }

    private static X509Certificate loadSingleCertificate(final String resourceName) {
        try (final InputStream stream = MultiTrustStoreX509TrustManagerTest.class.getResourceAsStream(resourceName)) {
            final X509Certificate[] certificates = MultiTrustStoreX509TrustManager.loadCertificates(stream);
            return certificates[0];
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void shouldNotInitiallyTrustSignedCertificate() {
        try {
            defaultTrustManager.checkServerTrusted(new X509Certificate[]{ signedCertificate }, "RSA");
            Assertions.fail("Expected exception not thrown");
        } catch (final CertificateException e) {
            // ignore
        }
    }

    @Test
    @Order(2)
    public void shouldTrustSignedCertificateAfterInstallingCaCertificate() {
        try {
            trustManagerWithCa.checkServerTrusted(new X509Certificate[]{ signedCertificate }, "RSA");
        } catch (final CertificateException e) {
            Assertions.fail("Unexpected exception thrown");
        }
    }

}
