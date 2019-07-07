package me.noobgam.pastie.main.jetty.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.noobgam.pastie.main.jetty.ExceptionResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ErrorAutoHandler extends AbstractHandler {

    protected final Logger logger = LogManager.getLogger(ErrorAutoHandler.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(
            String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        try {
            handle2(new RequestContext(target, baseRequest, request, response));
        } catch (Exception e) {
            logger.error("Exception occured: {}", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            baseRequest.setHandled(true);
            response.getWriter().println(
                    mapper.writeValueAsString(new ExceptionResponse(e))
            );
            throw e;
        }
    }

    public void handle2(
            RequestContext requestContext
    ) throws IOException, ServletException {
        // stub to allow overriding handle.
    }
}
