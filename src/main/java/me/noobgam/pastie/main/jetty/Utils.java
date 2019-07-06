package me.noobgam.pastie.main.jetty;

import java.util.Random;

public final class Utils {
    private Utils() {
    }

    private static ThreadLocal<Random> R =
            ThreadLocal.withInitial(
                    Random::new
            );

    public static String getRandomString(int len, String alphabet) {
        Random r = R.get();
        char[] buf = new char[len];
        for (int i = 0; i < len; ++i) {
            buf[i] = alphabet.charAt(r.nextInt(alphabet.length()));
        }
        return new String(buf);
    }

    public static String getRandomAlNum(int len) {
        return getRandomString(len, AlphaUtils.ALPHANUM);
    }
}
