package me.noobgam.pastie.main.background;

import me.noobgam.pastie.junk.JunkContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JunkContextConfiguration.class)
public class SchedulerContextConfiguration {
    @Bean
    public Scheduler scheduler() {
        return new Scheduler();
    }
}
