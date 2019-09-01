package me.noobgam.pastie.main.api;

import com.mongodb.async.client.MongoClient;
import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.main.jetty.dto.SuccessResponse;
import me.noobgam.pastie.main.jetty.helpers.*;
import me.noobgam.pastie.main.jetty.helpers.handlers.AuthAction;
import me.noobgam.pastie.main.users.cookies.CookieDao;
import me.noobgam.pastie.main.users.security.UserPasswordDao;
import me.noobgam.pastie.main.users.security.UserPasswordMongoDao;
import me.noobgam.pastie.main.users.user.User;
import me.noobgam.pastie.main.users.user.UserDao;
import me.noobgam.pastie.main.users.user.UserMongoDao;
import me.noobgam.pastie.utils.MongoClientUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ActionContainer("/register")
@Pipeline(AuthAction.class)
public class RegisterAction implements AbstractHandler2 {

    private static final String PASS_PREFIX = "PM:";

    private static final String HANDLE_MISSING = "Handle is missing or invalid";

    private static final String PASSWORD_MISSING = "Password is missing or invalid";

    private static final String ALREADY_TAKEN = "Handle already taken";

    private static final String COOKIE_DOMAIN = PropertiesHolder.getProperty("cookie.domain");

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPasswordDao userPasswordDao;

    @Autowired
    private CookieDao cookieDao;

    @Autowired
    private MongoClient mongoClient;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        if (requestContext instanceof AuthenticatedRequestContextHolder) {
            User user = ((AuthenticatedRequestContextHolder) requestContext).getUser();
            if (user != User.DUMMY) {
                throw new IllegalArgumentException("Already authenticated.");
            }
        }
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
        if (userO.isPresent()) {
            throw new IllegalArgumentException(ALREADY_TAKEN);
        }
        ObjectId newUserId = ObjectId.get();

        MongoClientUtils.inTransaction(
                mongoClient,
                session -> {
                    CompletableFuture<Void> future1 =
                            ((UserMongoDao) userDao).createUser(new User(newUserId, handle), session);
                    CompletableFuture<Void> future2 =
                            ((UserPasswordMongoDao) userPasswordDao).createCredentials(
                                    newUserId,
                                    DigestUtils.sha3_256(password),
                                    session
                            );
                    CompletableFuture.allOf(future1, future2).join();
                }
        );
        requestContext.addCookie(
                CookieUtils.generateAuthCookie(newUserId, cookieDao)
        );
        requestContext.addCookie(
                CookieUtils.generateUsernameCookie(handle)
        );
        requestContext.success(SuccessResponse.success());
        return requestContext;
    }
}
