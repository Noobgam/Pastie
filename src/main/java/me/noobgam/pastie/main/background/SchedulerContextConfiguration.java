package me.noobgam.pastie.main.background;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SchedulerContextConfiguration {
    @Bean
    public Scheduler scheduler(List<Job> jobs) {
        return new SimpleMultiThreadScheduler(jobs);
    }
}
