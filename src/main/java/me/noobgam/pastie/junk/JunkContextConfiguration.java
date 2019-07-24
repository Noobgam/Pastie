package me.noobgam.pastie.junk;

import me.noobgam.pastie.junk.twitch.TwitchViewCountPullJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JunkContextConfiguration {
    @Bean
    public TwitchViewCountPullJob twitchViewCountPullJob() {
        return new TwitchViewCountPullJob();
    }
}
