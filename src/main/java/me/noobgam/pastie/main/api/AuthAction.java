package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.helpers.ErrorAutoHandler;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.users.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

@ActionContainer("/auth")
public class AuthAction extends ErrorAutoHandler {

    @Autowired
    private UserDao userDao;

    @Override
    public void handle2(RequestContext requestContext) throws IOException, ServletException {
        Map<String, String> params = requestContext.getUrlParams();
        String handle = params.get("handle");
        if (handle == null) {
            throw new IllegalArgumentException("Empty handle");
        }

        //String password
    }
}
