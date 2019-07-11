package me.noobgam.pastie.core;

import me.noobgam.pastie.main.core.ReadinessChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractCache<T> implements Runnable {

    @Autowired
    private ReadinessChecker readinessChecker;

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static AtomicInteger cachesNumber = new AtomicInteger(0);
    private static AtomicInteger cachesReady = new AtomicInteger(0);

    private volatile boolean stop = false;
    private volatile T cached = null;

    private final Thread thread;

    public AbstractCache() {

        thread = new Thread(this, this.getClass().getCanonicalName());
    }


    @PostConstruct
    public void init() {
        if (waitForCacheOnStartup()) {
            if (cachesNumber.addAndGet(1) != cachesReady.get()) {
                readinessChecker.setCachesReady(false);
            }
        }
        if (cachesNumber.get() == cachesReady.get()) {
            readinessChecker.setCachesReady(true);
        }
        thread.start();
    }

    public T getCached() {
        if (cached == null) {
            throw new RuntimeException("No cached value");
        }
        return cached;
    }

    @Override
    public void run() {
        Duration delay = defaultDelay();
        boolean notCached = waitForCacheOnStartup();
        while (!stop) {
            try {
                Thread.sleep(10000L);
                cached = getCurrentValue();
                if (notCached) {
                    notCached = false;
                    if (cachesReady.addAndGet(1) == cachesNumber.get()) {
                        readinessChecker.setCachesReady(true);
                    }
                }
            } catch (Exception ex) {
                logger.error("Cache {} could not be updated. {}", this.getClass().getCanonicalName(), ex);
            }
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                stop = true;
            }
        }
    }

    public void stop() {
        if (!this.stop) {
            this.stop = true;
            thread.interrupt();
        }
    }

    protected boolean waitForCacheOnStartup() {
        return false;
    }

    protected abstract T getCurrentValue();

    protected abstract Duration defaultDelay();
}
