package me.noobgam.pastie.utils;

public final class ThreadUtils {
    private ThreadUtils() {

    }

    public static void sleepForever() {
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
