package me.noobgam.pastie.main.api;

import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.users.cookies.CookieDao;
import me.noobgam.pastie.main.users.security.UserPasswordDao;
import me.noobgam.pastie.main.users.user.User;
import me.noobgam.pastie.main.users.user.UserDao;
import me.noobgam.pastie.utils.RandomUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@ActionContainer("/login")
public class LoginAction implements AbstractHandler2 {

    private static final String PASS_PREFIX = "PM:";

    private static final String HANDLE_MISSING = "Handle is missing or invalid";

    private static final String PASSWORD_MISSING = "Password is missing or invalid";

    private static final String BAD_PAIR = "Unrecognized handle/password pair";

    private static final String COOKIE_DOMAIN = PropertiesHolder.getProperty("cookie.domain");

    @Autowired
    private UserDao userDao;

    @Autowired
    private CookieDao cookieDao;

    @Autowired
    private UserPasswordDao userPasswordDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        Map<String, String> params = requestContext.getUrlParams();
        String handle = params.get("handle");
        if (handle == null) {
            throw new IllegalArgumentException(HANDLE_MISSING);
        }

        // Password is SHA3-ed once again to ensure I never store users input.
        //  even if he overrides prefix explicitly.
        // Password passed to this function should normally look like
        //  PREFIX + MD5(USER_INPUT)
        String password = requestContext.getRequest().getReader().lines().findFirst().orElse(null);
        if (password == null || !password.startsWith(PASS_PREFIX)) {
            throw new IllegalArgumentException(PASSWORD_MISSING);
        }

        Optional<User> userO = userDao.findByUsername(handle).join();
        if (userO.isEmpty()) {
            throw new IllegalArgumentException(BAD_PAIR);
        }

        boolean validPair = userPasswordDao.validateCredentialPair(
                userO.get().getId(),
                DigestUtils.sha3_256(password)
        ).join();

        if (!validPair) {
            throw new IllegalArgumentException(BAD_PAIR);
        }

        requestContext.addCookie(
                generateCookie(userO.get().getId())
        );
        //requestContext.redirect("https://paste.noobgam.me", SuccessResponse.success());

        return requestContext;
    }

    private Cookie generateCookie(ObjectId objectId) {
        Cookie cookie = new Cookie(
                "SID",
                RandomUtils.generateSecureString()
        );
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setHttpOnly(true);

        cookieDao.storeCookie(objectId, cookie).join();

        return cookie;
    }
}
