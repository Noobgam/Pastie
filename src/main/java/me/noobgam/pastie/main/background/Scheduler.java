package me.noobgam.pastie.main.background;

import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler {

    private final Logger logger = LogManager.getLogger(Scheduler.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(30);

    @Autowired
    private List<Job> jobs;

    private Long2ObjectRBTreeMap<Job> scheduledJobs = new Long2ObjectRBTreeMap<>();

    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    private final Thread runThread = new Thread(this::run);

    /**
     * @return time to sleep (ms)
     * @throws InterruptedException
     */
    private long sleepBeforeJob() {
        final long firstTs;
        synchronized (lock) {
            if (scheduledJobs.isEmpty()) {
                return 1000;
            }
            firstTs = scheduledJobs.firstLongKey();
        }
        long now = Instant.now().toEpochMilli();
        long timeToSleep = Math.min(500L, firstTs - now);
        if (timeToSleep > 0) {
            return (timeToSleep + 1) / 2;
        }
        return 0;
    }

    private void run() {
        for (Job job : jobs) {
            addJob(job);
        }
        // while not interrupted.
        while (true) {
            try {
                long sleep = sleepBeforeJob();
                if (sleep > 0) {
                    Thread.sleep(sleep);
                    continue;
                }
                final Job jobToExecute;
                synchronized (lock) {
                    long key = scheduledJobs.firstLongKey();
                    jobToExecute = scheduledJobs.get(key);
                    scheduledJobs.remove(key);
                }
                executor.execute(() -> runJob(jobToExecute));
            } catch (InterruptedException ex) {
                logger.warn("Scheduler was interrupted, finalizing", ex);
                break;
            }
        }
    }

    public void runJob(Job job) {
        job.execute();
        addJob(job);
    }

    public void addJob(Job job) {
        // while true to make sure you don't override jobs.
        while (true) {
            synchronized (lock) {
                long now = Instant.now().toEpochMilli();
                long ts = now + job.delay().toMillis();
                if (scheduledJobs.get(ts) == null) {
                    scheduledJobs.put(ts, job);
                    return;
                }
            }
        }
    }

    @PostConstruct
    public void start() {
        runThread.start();
    }

    public void stop() {
        executor.shutdown();
        runThread.interrupt();
    }
}
