package me.noobgam.pastie.main.jetty.helpers;

import me.noobgam.pastie.main.jetty.RequestResponse;
import me.noobgam.pastie.main.users.user.User;
import org.eclipse.jetty.server.Request;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AuthenticatedRequestContextHolder implements RequestContext {

    private final User user;
    private final RequestContextHolder holder;

    public AuthenticatedRequestContextHolder(
            RequestContextHolder holder,
            User user
    ) {
        this.holder = holder;
        this.user = user;
    }

    @Override
    public Map<String, String> getUrlParams() {
        return holder.getUrlParams();
    }

    @Override
    @Nullable
    public String getHeader(String header) {
        return holder.getHeader(header);
    }

    @Override
    public String getTarget() {
        return holder.getTarget();
    }

    @Override
    public Request getBaseRequest() {
        return holder.getBaseRequest();
    }

    @Override
    public HttpServletRequest getRequest() {
        return holder.getRequest();
    }

    @Override
    @Nullable
    public Cookie getCookie(String cookie) {
        return holder.getCookie(cookie);
    }

    @Override
    public void addCookie(Cookie cookie) {
        holder.addCookie(cookie);
    }

    @Override
    public HttpServletResponse getResponse() {
        return holder.getResponse();
    }

    @Override
    public void success(RequestResponse requestResponse) {
        holder.success(requestResponse);
    }

    @Override
    public void respond(Integer status, RequestResponse result) {
        holder.respond(status, result);
    }

    @Override
    public void redirect(String path, RequestResponse result) {
        holder.redirect(path, result);
    }

    @Override
    public boolean isHandled() {
        return holder.isHandled();
    }

    public User getUser() {
        return user;
    }
}
