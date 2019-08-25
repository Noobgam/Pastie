package me.noobgam.pastie.main.background.cache;

import me.noobgam.pastie.main.background.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class UpdateCacheJob<T> extends Job {

    private static final Logger logger = LogManager.getLogger(UpdateCacheJob.class);

    private final AbstractCachedSomething<T> cachedSomething;

    private final String cacheName;

    UpdateCacheJob(
            AbstractCachedSomething<T> cachedSomething
    ) {
        this.cachedSomething = cachedSomething;
        cacheName = cachedSomething.getClass().getName();
    }

    @Override
    protected Duration delay() {
        return cachedSomething.defaultDelay();
    }

    @Override
    protected void run() {
        try {
            Instant start = Instant.now();
            logger.info("Started calculating cache {}.", cacheName);
            Optional<T> value = cachedSomething.calculateCurrent();
            synchronized (cachedSomething) {
                cachedSomething.value = value.orElse(null);
            }
            logger.info(
                    "Finished calculating cache {} in {}ms.",
                    cacheName,
                    Duration.between(start, Instant.now()).toMillis()
            );
        } catch (Exception e) {
            logger.error("Cache {} could not be updated. {}", cacheName, e);
            cachedSomething.value = null;
        }
    }
}
