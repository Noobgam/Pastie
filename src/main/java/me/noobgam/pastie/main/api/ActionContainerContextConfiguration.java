package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.core.ReadinessChecker;
import me.noobgam.pastie.main.core.ReadinessCheckerContextConfiguration;
import me.noobgam.pastie.main.jetty.helpers.handlers.AuxiliaryHandlersContextConfiguration;
import me.noobgam.pastie.main.paste.PasteDaoContextConfiguration;
import me.noobgam.pastie.main.users.cookies.CookieDaoContextConfiguration;
import me.noobgam.pastie.main.users.security.UserPasswordDaoContextConfiguration;
import me.noobgam.pastie.main.users.user.UserDaoContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        UserDaoContextConfiguration.class,
        PasteDaoContextConfiguration.class,
        CookieDaoContextConfiguration.class,
        ReadinessCheckerContextConfiguration.class,
        UserPasswordDaoContextConfiguration.class,

        AuxiliaryHandlersContextConfiguration.class
})
public class ActionContainerContextConfiguration {

    @Bean
    public LoginAction loginAction() {
        return new LoginAction();
    }

    @Bean
    public PostPasteAction postPasteAction() {
        return new PostPasteAction();
    }

    @Bean
    public PingAction pingAction(ReadinessChecker readinessChecker) {
        return new PingAction(readinessChecker);
    }

    @Bean
    public GetPasteAction getPasteAction() {
        return new GetPasteAction();
    }

    @Bean
    public RegisterAction registerAction() {
        return new RegisterAction();
    }
}
