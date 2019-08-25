package me.noobgam.pastie.main.background.cache;

import me.noobgam.pastie.main.background.Scheduler;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;

public abstract class AbstractCachedSomething<T> {

    @Nullable
    volatile T value;

    /**
     * @param scheduler scheduler to add a job to
     */
    protected AbstractCachedSomething(Scheduler scheduler) {
        if (cacheOnStartup()) {
            scheduler.addJobWithoutDelay(
                    new UpdateCacheJob<T>(this)
            );
        } else {
            scheduler.addJob(
                    new UpdateCacheJob<T>(this)
            );
        }
    }

    public Optional<T> getCached() {
        return Optional.ofNullable(value);
    }

    public abstract Optional<T> calculateCurrent();

    public abstract Duration defaultDelay();

    protected boolean cacheOnStartup() {
        return false;
    }
}
