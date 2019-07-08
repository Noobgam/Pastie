package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.core.ReadinessChecker;
import me.noobgam.pastie.main.core.ReadinessCheckerContextConfiguration;
import me.noobgam.pastie.main.paste.PasteDaoContextConfiguration;
import me.noobgam.pastie.main.users.UserDaoContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        UserDaoContextConfiguration.class,
        PasteDaoContextConfiguration.class,
        ReadinessCheckerContextConfiguration.class
})
public class ActionContainerContextConfiguration {
    @Bean
    public AuthAction authAction() {
        return new AuthAction();
    }

    @Bean
    public PostPasteAction postPasteAction() {
        return new PostPasteAction();
    }

    @Bean
    public PingAction pingAction(ReadinessChecker readinessChecker) {
        return new PingAction(readinessChecker);
    }
}
