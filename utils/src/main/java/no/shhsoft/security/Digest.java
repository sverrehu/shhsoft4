package no.shhsoft.security;

import no.shhsoft.utils.HexUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Digest {

    private Digest() {
        /* not to be instantiated */
    }

    private static byte[] digest(final String algorithm, final byte[] bytes) {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(bytes);
            return md.digest();
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Message digest `" + algorithm + "' not available");
        }
    }

    public static byte[] md5Bytes(final String s) {
        return digest("MD5", s.getBytes());
    }

    public static String md5(final String s) {
        return HexUtils.bytesToHexString(md5Bytes(s));
    }

    public static byte[] md5Bytes(final byte[] bytes) {
        return digest("MD5", bytes);
    }

    public static String md5(final byte[] bytes) {
        return HexUtils.bytesToHexString(md5Bytes(bytes));
    }

    public static byte[] sha1Bytes(final String s) {
        return digest("SHA1", s.getBytes());
    }

    public static String sha1(final String s) {
        return HexUtils.bytesToHexString(sha1Bytes(s));
    }

    public static byte[] sha1Bytes(final byte[] bytes) {
        return digest("SHA1", bytes);
    }

    public static String sha1(final byte[] bytes) {
        return HexUtils.bytesToHexString(sha1Bytes(bytes));
    }

    public static byte[] sha256Bytes(final String s) {
        return digest("SHA-256", s.getBytes());
    }

    public static String sha256(final String s) {
        return HexUtils.bytesToHexString(sha256Bytes(s));
    }

    public static byte[] sha256Bytes(final byte[] bytes) {
        return digest("SHA-256", bytes);
    }

    public static String sha256(final byte[] bytes) {
        return HexUtils.bytesToHexString(sha256Bytes(bytes));
    }

    public static byte[] sha512Bytes(final String s) {
        return digest("SHA-512", s.getBytes());
    }

    public static String sha512(final String s) {
        return HexUtils.bytesToHexString(sha512Bytes(s));
    }

    public static byte[] sha512Bytes(final byte[] bytes) {
        return digest("SHA-512", bytes);
    }

    public static String sha512(final byte[] bytes) {
        return HexUtils.bytesToHexString(sha512Bytes(bytes));
    }

}
