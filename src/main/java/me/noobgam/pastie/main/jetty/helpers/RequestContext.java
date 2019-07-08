package me.noobgam.pastie.main.jetty.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.noobgam.pastie.main.jetty.RequestResponse;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class RequestContext {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final String target;
    private final Request baseRequest;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final Instant start;

    public RequestContext(
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

    public Map<String, String> getUrlParams() {
        return QueryUtils.splitToUrlParams(request.getQueryString());
    }

    public String getTarget() {
        return target;
    }

    public Request getBaseRequest() {
        return baseRequest;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void success(RequestResponse requestResponse) {
        respond(200, requestResponse);
    }

    public void respond(Integer status, RequestResponse result) {
        try {
            result.setHandleMs(Duration.between(start, Instant.now()).toMillis());
            response.setStatus(status);
            response.getWriter().println(mapper.writeValueAsString(result));
            baseRequest.setHandled(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
