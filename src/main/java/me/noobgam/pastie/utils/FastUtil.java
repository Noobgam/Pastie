package me.noobgam.pastie.utils;

public class FastUtil {
    private FastUtil() {

    }

    public static int initalCapacityForHashmap(int n) {
        return (int) ((n / 0.75) + 1);
    }
}
