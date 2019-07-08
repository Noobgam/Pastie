package me.noobgam.pastie.main.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReadinessCheckerContextConfiguration {
    @Bean
    public ReadinessChecker readinessChecker() {
        return new ReadinessChecker();
    }
}
