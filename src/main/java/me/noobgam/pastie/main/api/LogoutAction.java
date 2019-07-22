package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.SuccessResponse;
import me.noobgam.pastie.main.jetty.helpers.*;
import me.noobgam.pastie.main.jetty.helpers.handlers.AuthAction;
import me.noobgam.pastie.main.users.cookies.CookieDao;
import me.noobgam.pastie.main.users.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;

@ActionContainer("/logout")
@Pipeline(AuthAction.class)
public class LogoutAction implements AbstractHandler2 {

    @Autowired
    private CookieDao cookieDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        AuthenticatedRequestContextHolder contextHolder = (AuthenticatedRequestContextHolder) requestContext;
        CookieUtils.discardAuth(requestContext);
        if (contextHolder.getUser() != User.DUMMY) {
            // we don't need to join future.
            cookieDao.deAuthUser(contextHolder.getUser().getId());
        }
        requestContext.success(SuccessResponse.success());
        return requestContext;
    }
}
