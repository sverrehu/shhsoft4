package no.shhsoft.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Base64UtilsPerformanceTestNot {

    private static byte[][] data;
    private static final int NUM_WARM_UP_ROUNDS = 300000;
    private static final int NUM_ROUNDS = 3000000;

    private Base64UtilsPerformanceTestNot() {
    }

    private interface EncoderDecoder {

        String getName();

        String encode(byte[] bytes);

        byte[] decode(String b64);

    }

    static final class MyEncoderDecoder
    implements EncoderDecoder {

        @Override
        public String getName() {
            return "Mine";
        }

        @Override
        public String encode(final byte[] bytes) {
            return Base64Utils.encode(bytes);
        }

        @Override
        public byte[] decode(final String b64) {
            return Base64Utils.decode(b64);
        }

    }

    static final class CommonsEncoderDecoder
    implements EncoderDecoder {

        private final Base64 base64 = new Base64();

        @Override
        public String getName() {
            return "Commons";
        }

        @Override
        public String encode(final byte[] bytes) {
            return base64.encodeToString(bytes);
        }

        @Override
        public byte[] decode(final String b64) {
            return base64.decode(b64);
        }

    }

    static final class ComboEncoderDecoder
    implements EncoderDecoder {

        private final Base64 base64 = new Base64();

        @Override
        public String getName() {
            return "Combo";
        }

        @Override
        public String encode(final byte[] bytes) {
            return Base64Utils.encode(bytes);
        }

        @Override
        public byte[] decode(final String b64) {
            return base64.decode(b64);
        }

    }

    private static void init() {
        data = new byte[256][];
        for (int q = 0; q < data.length; q++) {
            data[q] = new byte[q];
            for (int w = 0; w < q; w++) {
                data[q][w] = (byte) w;
            }
        }
    }

    private static void test(final EncoderDecoder encoderDecoder, final int numRounds, final boolean report) {
        long t = System.currentTimeMillis();
        int row = 0;
        for (int q = numRounds - 1; q >= 0; q--) {
            final String encoded = encoderDecoder.encode(data[row]);
            final byte[] decoded = encoderDecoder.decode(encoded);
            Base64UtilsTest.assertEqualsWithLength(null, data[row], decoded, data[row].length);
            if (++row >= data.length) {
                row = 0;
            }
        }
        t = System.currentTimeMillis() - t;
        if (report) {
            System.out.println(encoderDecoder.getName() + ": " + numRounds + " in " + t + " ms");
        }
    }

    public static void main(final String[] args) {
        init();
        final MyEncoderDecoder myEncoderDecoder = new MyEncoderDecoder();
        final CommonsEncoderDecoder commonsEncoderDecoder = new CommonsEncoderDecoder();
        final ComboEncoderDecoder comboEncoderDecoder = new ComboEncoderDecoder();
        System.out.println("Warming up");
        test(commonsEncoderDecoder, NUM_WARM_UP_ROUNDS, false);
        test(myEncoderDecoder, NUM_WARM_UP_ROUNDS, false);
        test(comboEncoderDecoder, NUM_WARM_UP_ROUNDS, false);
        System.out.println("Testing");
        test(commonsEncoderDecoder, NUM_ROUNDS, true);
        test(myEncoderDecoder, NUM_ROUNDS, true);
        test(comboEncoderDecoder, NUM_ROUNDS, true);
    }

}
