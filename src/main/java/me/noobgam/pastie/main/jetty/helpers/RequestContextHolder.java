package me.noobgam.pastie.main.jetty.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.noobgam.pastie.main.jetty.dto.RequestResponse;
import org.eclipse.jetty.server.Request;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class RequestContextHolder implements RequestContext {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final String target;
    private final Request baseRequest;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final Instant start;

    public RequestContextHolder(
            String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        start = Instant.now();

        this.target = target;
        this.baseRequest = baseRequest;
        this.request = request;
        this.response = response;
    }

    @Override
    public Map<String, String> getUrlParams() {
        if (request.getQueryString() == null) {
            return Collections.emptyMap();
        }
        return QueryUtils.splitToUrlParams(request.getQueryString());
    }

    @Override
    @Nullable
    public String getHeader(String header) {
        return request.getHeader(header);
    }

    @Nullable
    @Override
    public Cookie getCookie(String cookie) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Stream.of(cookies).filter(c -> c.getName().equals(cookie))
                .findAny().orElse(null);
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public Request getBaseRequest() {
        return baseRequest;
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public void success(RequestResponse requestResponse) {
        respond(200, requestResponse);
    }

    @Override
    public void respond(Integer status, RequestResponse result) {
        try {
            response.setContentType("application/json");
            fillHeaders(status, response);
            result.setHandleMs(Duration.between(start, Instant.now()).toMillis());
            result.setStatus(status);
            response.setStatus(status);
            response.getWriter().println(mapper.writeValueAsString(result));
            baseRequest.setHandled(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void respondPlainText(Integer status, String result) {
        try {
            response.setContentType("text/plain");
            fillHeaders(status, response);
            response.setStatus(status);
            response.getWriter().print(result);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void fillHeaders(int status, HttpServletResponse response) {
        // for localhost testing rewrite local domain resolving rules.
        response.addHeader("Access-Control-Allow-Origin", "paste.noobgam.me");

        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "X-Paste-Lang");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(status);
    }

    @Override
    public void redirect(String path, RequestResponse result) {
        try {
            respond(302, result);
            response.sendRedirect(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isHandled() {
        return baseRequest.isHandled();
    }
}
