package me.noobgam.pastie.main.background;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public abstract class Job {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public void execute() {
        try {
            run();
        } catch (Exception ex) {
            logger.error("Job failed with unhandled exception", ex);
        }
    }

    protected abstract Duration delay();

    protected abstract void run();
}
