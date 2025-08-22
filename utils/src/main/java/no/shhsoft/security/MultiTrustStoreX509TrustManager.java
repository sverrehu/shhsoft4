package no.shhsoft.security;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class MultiTrustStoreX509TrustManager
implements X509TrustManager {

    private final List<X509TrustManager> trustManagers = new ArrayList<>();

    private MultiTrustStoreX509TrustManager() {
    }

    public static MultiTrustStoreX509TrustManager withDefaultTrustStore() {
        final MultiTrustStoreX509TrustManager instance = new MultiTrustStoreX509TrustManager();
        instance.trustManagers.add(getDefaultTrustManager());
        return instance;
    }

    public static MultiTrustStoreX509TrustManager withoutDefaultTrustStore() {
        return new MultiTrustStoreX509TrustManager();
    }

    public MultiTrustStoreX509TrustManager installAsDefault() {
        try {
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { this }, null);
            SSLContext.setDefault(context);
            return this;
        } catch (final NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType)
    throws CertificateException {
        CertificateException exception = null;
        for (final X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkClientTrusted(chain, authType);
                exception = null;
                break;
            } catch (final CertificateException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType)
    throws CertificateException {
        CertificateException exception = null;
        for (final X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                exception = null;
                break;
            } catch (final CertificateException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final List<X509Certificate> issuers = new ArrayList<>();
        for (final X509TrustManager trustManager : trustManagers) {
            issuers.addAll(List.of(trustManager.getAcceptedIssuers()));
        }
        return issuers.toArray(new X509Certificate[0]);
    }

    public MultiTrustStoreX509TrustManager withCaCertificate(final X509Certificate certificate) {
        return withTrustStore(toTrustStore(certificate));
    }

    public MultiTrustStoreX509TrustManager withCaCertificateFile(final String filename) {
        try (final InputStream stream = new FileInputStream(filename)) {
            final X509Certificate[] certificates = loadCertificates(stream);
            return withTrustStore(toTrustStore(certificates));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MultiTrustStoreX509TrustManager withCaCertificateResource(final String resourceName) {
        try (final InputStream stream = MultiTrustStoreX509TrustManager.class.getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new RuntimeException("Resource " + resourceName + " not found");
            }
            final X509Certificate[] certificates = loadCertificates(stream);
            return withTrustStore(toTrustStore(certificates));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MultiTrustStoreX509TrustManager withTrustStore(final KeyStore truststore) {
        trustManagers.add(toTrustManager(truststore));
        return this;
    }

    static X509Certificate[] loadCertificates(final InputStream input) {
        try {
            final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            final Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(input);
            final X509Certificate[] x509Certificates = new X509Certificate[certificates.size()];
            int index = 0;
            for (final Certificate certificate : certificates) {
                x509Certificates[index++] = (X509Certificate) certificate;
            }
            return x509Certificates;
        } catch (final CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyStore toTrustStore(final X509Certificate certificate) {
        return toTrustStore(new X509Certificate[] { certificate });
    }

    private KeyStore toTrustStore(final X509Certificate[] certificates) {
        try {
            final KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            final char[] password = "changeit".toCharArray();
            truststore.load(null, password);
            for (final X509Certificate certificate : certificates) {
                truststore.setCertificateEntry(String.valueOf(certificate.getSerialNumber()), certificate);
            }
            return truststore;
        } catch (final KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private static X509TrustManager getDefaultTrustManager() {
        return toTrustManager(null);
    }

    private static X509TrustManager toTrustManager(final KeyStore truststore) {
        try {
            final TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(truststore);
            return findX509TrustManager(factory);
        } catch (final NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static X509TrustManager findX509TrustManager(final TrustManagerFactory factory) {
        for (final TrustManager trustManager : factory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new RuntimeException("No X509TrustManager found.");
    }

}
