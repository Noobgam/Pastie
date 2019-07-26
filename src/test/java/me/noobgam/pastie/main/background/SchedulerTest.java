package me.noobgam.pastie.main.background;


import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerTest {

    private static class TestJob extends Job {

        private volatile Duration delay;
        private final int id;

        private final Collection<Integer> storage;

        TestJob(Duration delay, int id, Collection<Integer> storage) {
            this.delay = delay;
            this.id = id;
            this.storage = storage;
        }

        @Override
        protected Duration delay() {
            return delay;
        }

        @Override
        protected void run() {
            storage.add(id);
            delay = Duration.ofMillis(Long.MAX_VALUE);
        }
    }

    @Test
    public void schedulerTest() throws Exception {
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

        int TEST_SIZE = 5;
        int TEST_DELAY = 20;


        ArrayList<Job> jobs = new ArrayList<>();
        for (int i = 1; i <= TEST_SIZE; ++i) {
            jobs.add(
                    new TestJob(Duration.ofMillis(TEST_DELAY * i), i, queue)
            );
        }
        for (int i = 1; i <= TEST_SIZE; ++i) {
            // these jobs are used to check strict job execution ordering
            jobs.add(
                    new TestJob(Duration.ofMillis(TEST_DELAY * TEST_SIZE + i), TEST_SIZE + i, queue)
            );
        }
        Scheduler scheduler = new Scheduler(jobs);
        scheduler.start();

        for (Integer i = 1; i < 10; ++i) {
            Thread.sleep(TEST_DELAY);
            if (scheduler.isRunning()) {
                break;
            }
        }

        for (Integer i = 1; i <= 2 * TEST_SIZE; ++i) {
            boolean done = false;
            for (int retry = 0; retry < 3; ++retry) {
                Thread.sleep(TEST_DELAY);
                Integer res = queue.peek();
                if (res == null) {
                    continue;
                }
                Assert.assertEquals("Scheduler executed jobs in wrong order", i, queue.poll());
                done = true;
                break;
            }
            Assert.assertTrue("Scheduler failed to execute a job in time", done);
        }
        scheduler.stop();
    }
}
