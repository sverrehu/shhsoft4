package no.shhsoft.utils;

import java.io.ByteArrayOutputStream;

/**
 * https://datatracker.ietf.org/doc/draft-faltstrom-base45/
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Base45Utils {

    private static final char[] CHARS_45 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".toCharArray();

    private Base45Utils() {
    }

    public static String encode(final byte[] data, final int len) {
        final StringBuilder sb = new StringBuilder(5 + len * 3 / 2);
        /* Two input bytes will give three output characters. */
        for (int idx = 0;;) {
            if (idx >= len) {
                break;
            }
            int b = ((int) data[idx++]) & 0xff;
            if (idx >= len) {
                addChar(sb, b, 2);
                break;
            }
            b = (b << 8) | (((int) data[idx++]) & 0xff);
            addChar(sb, b, 3);
        }
        return sb.toString();
    }

    private static void addChar(final StringBuilder sb, final int b, final int n) {
        int bitsLeft = b;
        for (int q = n - 1; q >= 0; q--) {
            sb.append(CHARS_45[bitsLeft % 45]);
            bitsLeft /= 45;
        }
    }

    public static byte[] decode(final String b45) {
        final int inputLength = b45.length();
        final ByteArrayOutputStream out = new ByteArrayOutputStream(inputLength * 2 / 3);
        final int[] b3 = new int[3];
        int b3index = 0;
        final char[] b45chars = b45.toCharArray();
        for (int q = 0; q < inputLength; q++) {
            final int c = b45chars[q];
            if (c == '\t' || c == '\r' || c == '\n') {
                continue;
            }
            final int value = findValue(c);
            if (value < 0) {
                throw new RuntimeException("Don't think this is supposed to happen");
            }
            if (b3index == b3.length) {
                addBytes(out, b3, b3index);
                b3index = 0;
            }
            b3[b3index++] = value;
        }
        addBytes(out, b3, b3index);
        return out.toByteArray();
    }

    public static String encode(final byte[] data) {
        return encode(data, data.length);
    }

    private static int findValue(final int c) {
        for (int q = CHARS_45.length - 1; q >= 0; q--) {
            if (CHARS_45[q] == c) {
                return q;
            }
        }
        throw new RuntimeException("Invalid character `" + (char) c + "' in BASE45 string");
    }

    private static void addBytes(final ByteArrayOutputStream out, final int[] bytes, final int len) {
        if (len <= 1) {
            return;
            //throw new RuntimeException("Unexpected length");
        }
        int value = 0;
        for (int q = len - 1; q >= 0; q--) {
            value = value * 45 + bytes[q];
        }
        if (len == 3) {
            out.write(value >> 8);
        }
        out.write(value & 0xff);
    }

    public static void main(final String[] args) {
        System.out.println(encode(StringUtils.getBytesUtf8("Hello!!")));
        System.out.println(StringUtils.newStringUtf8(decode("%69 VD92EX0")));
        System.out.println(StringUtils.newStringUtf8(decode("QED8WEX0")));
        final String s = "NO1:NCF8B0*50T9WUWGSLKH47GO0RX48M2$K2MQG8CKZ9C*70M+9FN01CCT NWY0HACQPDD97TK0G90XJC/$ENF6OF63W5Q47A46JPCT3E:TCLA7LB7..DX%DZJC0/DBIA5NA JC/.D0H9LPCG/D1PCC1AMPCG/DU6VO/EZKEZ96446F56ZKEHEC-3E:10O802Y79TRYDR-EH7ELT8P7/8VF5F9M*B5S3I1:851V/AVYDH+PJATC8236U45CM45WM5PIECQYUW%HSH10OMK734LCX8N/*FJMLJ9C/PT%-FW45WUK%4L46V2V2VJ8I%8-Q50AIY EW8RO+ABQ8S6C XHUD0$Q1HRSKE5O/H4PH9D4EX0DWKRUN3AV27NI+44U40GA6IU6OSPU5$T9KUBBO5*36KBE1+4PXTHQD-CWSBRI24K6SHJBHSP7GC ASRBS-0T.4THF5-3RVP7KM95G8B0P/4C5WU $TR5KJ6B67D5JGO$BC5ACG83PQP9KTCIL+OB2ABVAYKGZXMUR54NFWI0.HN.BM1T4R.2L-1LZLW59SOVV:8V4ILNCBB99DRQDN0%4M/RK3CNI9S$GYK1T-3XAGF.QTKGI99GX0P/4WILI$FB 0$EAUCF-NSW502%CTUBHZPY23G$I9FKB1UL/VYLV95WAAGVD3P+82/C:QHG*K +U9B34%JW*KFF2/Q2IGTWNUBRUES7XSH:469RDSNJPYTODKIUK9J9+/8D+V*3CXA4UVL1-DKW1JB9D62NSER0H0/R";
        System.out.println(StringUtils.newStringUtf8(decode(s)));
    }

}
