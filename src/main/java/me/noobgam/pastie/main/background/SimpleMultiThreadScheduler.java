package me.noobgam.pastie.main.background;

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleMultiThreadScheduler implements Scheduler {

    private final Logger logger = LogManager.getLogger(SimpleMultiThreadScheduler.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(30);

    private final List<Job> jobs;

    public SimpleMultiThreadScheduler(List<Job> jobs) {
        this.jobs = jobs;
    }

    private ObjectRBTreeSet<ScheduledJob> scheduledJobs = new ObjectRBTreeSet<>();

    private final Lock lock = new ReentrantLock();

    private final Thread runThread = new Thread(this::run);

    private volatile boolean running = false;

    /**
     * @return first job, blocks until job is found.
     * @throws InterruptedException
     */
    private ScheduledJob getJob() throws InterruptedException {
        synchronized (lock) {
            while (scheduledJobs.isEmpty()) {
                lock.wait();
            }
            return scheduledJobs.first();
        }
    }

    /**
     * @return popped job, blocks until one is found.
     * @throws InterruptedException
     */
    private ScheduledJob popJob() throws InterruptedException {
        final ScheduledJob job;
        synchronized (lock) {
            while (scheduledJobs.isEmpty()) {
                lock.wait();
            }
            job = scheduledJobs.first();
            scheduledJobs.remove(job);
        }
        return job;
    }

    private void run() {
        for (Job job : jobs) {
            addJob(job);
        }
        // while not interrupted.
        running = true;
        while (true) {
            try {
                ScheduledJob job = getJob();
                Instant now = Instant.now();
                if (now.isBefore(job.getScheduleTime())) {
                    long sleepMs =
                            Math.min(
                                    500L,
                                    Duration.between(
                                            now,
                                            job.getScheduleTime()
                                    ).toMillis()
                            );
                    Thread.sleep(sleepMs);
                    continue;
                }
                final Job jobToExecute = popJob().getJob();
                executor.execute(() -> runJob(jobToExecute));
            } catch (InterruptedException ex) {
                logger.warn("Scheduler was interrupted, finalizing", ex);
                break;
            }
        }
    }

    private void runJob(Job job) {
        job.execute();
        addJob(job);
    }

    @Override
    public void addJob(Job job) {
        Duration delay = job.delay();
        if (delay == null) {
            return;
        }
        Instant ts = Instant.now().plus(delay);
        synchronized (lock) {
            scheduledJobs.add(new ScheduledJob(job, ts));
            lock.notify();
        }
    }

    @Override
    public void addJobWithoutDelay(Job job) {
        Instant ts = Instant.now();
        synchronized (lock) {
            scheduledJobs.add(new ScheduledJob(job, ts));
            lock.notify();
        }
    }

    @Override
    @PostConstruct
    public void start() {
        runThread.start();
    }

    @Override
    public void stop() {
        executor.shutdown();
        runThread.interrupt();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
