package me.noobgam.pastie.main.jetty.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.noobgam.pastie.core.env.Environment;
import me.noobgam.pastie.main.jetty.ExceptionResponse;
import me.noobgam.pastie.main.jetty.SuccessResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ErrorAutoHandler extends AbstractHandler {

    private final Logger logger = LogManager.getLogger(ErrorAutoHandler.class);

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
        } catch (IllegalArgumentException e) {
            logger.error("Exception occurred: {}", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            baseRequest.setHandled(true);
            response.getWriter().println(
                    mapper.writeValueAsString(new ExceptionResponse(e))
            );
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            baseRequest.setHandled(true);
            if (Environment.ENV != Environment.Type.PROD) {
                response.getWriter().println(
                        mapper.writeValueAsString(new ExceptionResponse(e))
                );
            } else {
                response.getWriter().println(
                        mapper.writeValueAsString(SuccessResponse.fail())
                );
            }
            throw e;
        }
    }

    public void handle2(
            RequestContext requestContext
    ) throws IOException, ServletException {
        // stub to allow overriding handle.
    }
}
