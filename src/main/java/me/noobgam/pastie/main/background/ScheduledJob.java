package me.noobgam.pastie.main.background;

import java.time.Instant;

public class ScheduledJob implements Comparable<ScheduledJob> {
    private final Job job;
    private final Instant scheduleTime;

    public ScheduledJob(Job job, Instant scheduleTime) {
        this.job = job;
        this.scheduleTime = scheduleTime;
    }

    public Job getJob() {
        return job;
    }

    public Instant getScheduleTime() {
        return scheduleTime;
    }

    @Override
    public int compareTo(ScheduledJob o) {
        return scheduleTime.compareTo(o.scheduleTime);
    }
}
