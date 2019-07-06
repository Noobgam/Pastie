package me.noobgam.pastie.main.jetty;

import java.util.Locale;

public final class AlphaUtils {
    private AlphaUtils() {
    }

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWER = UPPER.toLowerCase(Locale.ROOT);

    public static final String DIGITS = "0123456789";

    public static final String ALPHANUM = UPPER + LOWER + DIGITS;
}
