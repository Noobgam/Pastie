package me.noobgam.pastie.main.core;

import java.util.function.Supplier;

public class ReadinessChecker implements Supplier<Boolean> {

    private volatile boolean cachesReady;

    public void setCachesReady(boolean cachesReady) {
        this.cachesReady = cachesReady;
    }

    @Override
    public Boolean get() {
        return cachesReady;
    }
}
