package me.noobgam.pastie.main.jetty.helpers.handlers;

import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.AuthenticatedRequestContextHolder;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.jetty.helpers.RequestContextHolder;
import me.noobgam.pastie.main.users.cookies.CookieDao;
import me.noobgam.pastie.main.users.user.User;
import me.noobgam.pastie.main.users.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.IOException;

public class AuthAction implements AbstractHandler2 {

    public static final String AUTH_HEADER = "SID";

    public static final String COOKIE_DOMAIN = ".paste.noobgam.me";

    @Autowired
    private CookieDao cookieDao;

    @Autowired
    private UserDao userDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        if (!(requestContext instanceof RequestContextHolder)) {
            throw new RuntimeException("Multiple authentifications");
        }
        javax.servlet.http.Cookie cookie = requestContext.getCookie(AUTH_HEADER);
        if (!cookieIsGood(cookie)) {
            return new AuthenticatedRequestContextHolder((RequestContextHolder) requestContext, User.DUMMY);
        }
        User user =
                cookieDao.findByCookie(cookie.getValue()).join()
                        .flatMap(c -> userDao.findById(c.getUserId()).join())
                        .orElse(User.DUMMY);
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
