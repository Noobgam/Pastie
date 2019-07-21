package me.noobgam.pastie.main.jetty.helpers.handlers;

import me.noobgam.pastie.main.jetty.helpers.*;
import me.noobgam.pastie.main.users.cookies.CookieDao;
import me.noobgam.pastie.main.users.user.User;
import me.noobgam.pastie.main.users.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.IOException;

public class AuthAction implements AbstractHandler2 {

    public static final String AUTH_HEADER = "SID";

    @Autowired
    private CookieDao cookieDao;

    @Autowired
    private UserDao userDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        if (requestContext instanceof AuthenticatedRequestContextHolder) {
            throw new RuntimeException("Multiple authentifications");
        }
        javax.servlet.http.Cookie cookie = requestContext.getCookie(AUTH_HEADER);
        if (!cookieIsGood(cookie)) {
            CookieUtils.discardAuth(requestContext);
            return new AuthenticatedRequestContextHolder((RequestContextHolder) requestContext, User.DUMMY);
        }
        User user =
                cookieDao.findByCookie(cookie.getValue()).join()
                        .flatMap(c -> userDao.findById(c.getUserId()).join())
                        .orElse(User.DUMMY);
        CookieUtils.authenticateUserNewCookie(requestContext, user, cookieDao);
        return new AuthenticatedRequestContextHolder(
                (RequestContextHolder) requestContext,
                user
        );
    }

    private static boolean cookieIsGood(@Nullable javax.servlet.http.Cookie cookie) {
        return cookie != null
                && cookie.isHttpOnly()
                && cookie.getSecure()
                && cookie.getValue().length() == 70;
    }
}
