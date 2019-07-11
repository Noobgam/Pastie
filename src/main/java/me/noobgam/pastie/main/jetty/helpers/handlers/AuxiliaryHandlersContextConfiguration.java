package me.noobgam.pastie.main.jetty.helpers.handlers;

import me.noobgam.pastie.main.users.cookies.CookieDaoContextConfiguration;
import me.noobgam.pastie.main.users.user.UserDaoContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        UserDaoContextConfiguration.class,
        CookieDaoContextConfiguration.class,
})
public class AuxiliaryHandlersContextConfiguration {
    @Bean
    public AuthAction authAction() {
        return new AuthAction();
    }
}
