package me.noobgam.pastie.main.jetty.helpers;

import me.noobgam.pastie.main.users.cookies.CookieDao;
import me.noobgam.pastie.main.users.user.User;
import me.noobgam.pastie.utils.RandomUtils;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;

public final class CookieUtils {

    public static final String COOKIE_DOMAIN = ".paste.noobgam.me";
    private static final Cookie DISCARD_AUTH;
    private static final Cookie DISCARD_SID;

    static {
        DISCARD_AUTH = new Cookie(
                "Username",
                null
        );
        DISCARD_AUTH.setMaxAge(0);
        DISCARD_AUTH.setDomain(COOKIE_DOMAIN);
        DISCARD_AUTH.setHttpOnly(true);
        DISCARD_AUTH.setPath("/");

        DISCARD_SID = new Cookie(
                "SID",
                null
        );
        DISCARD_SID.setMaxAge(0);
        DISCARD_SID.setDomain(COOKIE_DOMAIN);
        DISCARD_SID.setPath("/");
    }

    private CookieUtils() {
    }

    public static void discardAuth(RequestContext requestContext) {
        requestContext.addCookie(DISCARD_AUTH);
        requestContext.addCookie(DISCARD_SID);
    }

    public static void authenticateUserNewCookie(
            RequestContext requestContext,
            User user,
            // can be null in case user is dummy.
            @Nullable CookieDao cookieDao
    ) {
        if (user == User.DUMMY) {
            discardAuth(requestContext);
            return;
        }
        requestContext.addCookie(
                generateAuthCookie(
                        user.getId(),
                        cookieDao
                )
        );
        requestContext.addCookie(
                generateUsernameCookie(user.getUsername())
        );
    }

    public static Cookie generateAuthCookie(ObjectId objectId, CookieDao cookieDao) {
        Cookie cookie = new Cookie(
                "SID",
                RandomUtils.generateSecureString()
        );
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        cookieDao.storeCookie(objectId, cookie).join();

        return cookie;
    }

    /**
     * dummy cookie to cache username client-side to show on UI
     */
    public static Cookie generateUsernameCookie(String name) {
        final Cookie cookie = new Cookie("Username", name);

        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");

        cookie.setHttpOnly(false);
        return cookie;
    }
}
