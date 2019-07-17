package me.noobgam.pastie.utils;

import java.security.SecureRandom;

public final class RandomUtils {
    private RandomUtils() {
    }

    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789".toCharArray();

    private static final int DEFAULT_LENGTH = 70;

    public static String generateSecureString() {
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder builder = new StringBuilder(DEFAULT_LENGTH);

        for (int i = 0; i < DEFAULT_LENGTH; ++i) {
            builder.append(CHARS[secureRandom.nextInt(CHARS.length)]);
        }

        return builder.toString();
    }
}
