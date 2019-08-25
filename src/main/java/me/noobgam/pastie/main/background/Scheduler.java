package me.noobgam.pastie.main.background;

public interface Scheduler {
    void addJob(Job job);

    void addJobWithoutDelay(Job job);

    void start();

    void stop();

    boolean isRunning();
}
