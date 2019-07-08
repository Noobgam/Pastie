package me.noobgam.pastie.main.jetty.helpers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class RequestWrapperHandler extends AbstractHandler {
    @Override
    public void handle(
            String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        handle2(new RequestContext(target, baseRequest, request, response));
    }

    public abstract void handle2(
            RequestContext requestContext
    ) throws IOException, ServletException;
}
