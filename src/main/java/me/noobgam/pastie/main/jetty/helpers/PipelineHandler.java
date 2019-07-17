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

public class PipelineHandler extends AbstractHandler {

    private static final Logger logger = LogManager.getLogger(PipelineHandler.class);

    protected static final ObjectMapper mapper = new ObjectMapper();

    private AbstractHandler2[] handlerPipeline;
    private final boolean errorHandling;

    public PipelineHandler(AbstractHandler2... handlerPipeline) {
        this(true, handlerPipeline);
    }

    public PipelineHandler(boolean autoErrorHandling, AbstractHandler2... handlerPipeline) {
        this.errorHandling = autoErrorHandling;
        this.handlerPipeline = handlerPipeline;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestContext context = new RequestContextHolder(target, baseRequest, request, response);
        for (AbstractHandler2 handler : handlerPipeline) {
            try {
                context = handler.handle(context);
                if (context.isHandled()) {
                    return;
                }
            } catch (IllegalArgumentException ex) {
                logger.error("Illegal request", ex);
                if (errorHandling) {
                    // flush context.
                    context = new RequestContextHolder(target, baseRequest, request, response);
                    context.respond(400, new ExceptionResponse(ex));
                } else {
                    throw ex;
                }
            } catch (Exception ex) {
                logger.error("Exception occurred", ex);
                if (errorHandling) {
                    // flush context.
                    context = new RequestContextHolder(target, baseRequest, request, response);
                    if (Environment.ENV != Environment.Type.PROD) {
                        context.respond(500, new ExceptionResponse(ex));
                    } else {
                        context.respond(500, SuccessResponse.fail());
                    }
                } else {
                    throw ex;
                }
            }
        }
    }
}
