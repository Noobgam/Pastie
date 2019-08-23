package me.noobgam.pastie.main.jetty.helpers;

import me.noobgam.pastie.main.jetty.RequestResponse;
import org.eclipse.jetty.server.Request;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface RequestContext {
    Map<String, String> getUrlParams();

    @Nullable
    String getHeader(String header);

    @Nullable
    Cookie getCookie(String cookie);

    void addCookie(Cookie cookie);

    String getTarget();

    Request getBaseRequest();

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    void success(RequestResponse requestResponse);

    void respond(Integer status, RequestResponse result);

    void respondPlainText(Integer status, String response);

    void redirect(String path, RequestResponse result);

    boolean isHandled();
}
