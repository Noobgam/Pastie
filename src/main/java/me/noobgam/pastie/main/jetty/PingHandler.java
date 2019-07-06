package me.noobgam.pastie.main.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;

public class PingHandler extends AbstractHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Supplier<Boolean> isReadyF;

    public PingHandler(Supplier<Boolean> isReadyF) {
        this.isReadyF = isReadyF;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean ready = isReadyF.get();
        response.setContentType("application/json");
        if (ready) {
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println(
                    mapper.writeValueAsString(mapper.writeValueAsString(new PingResponse()))
            );
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            baseRequest.setHandled(true);
            response.getWriter().println(mapper.writeValueAsString(new PingResponse()));
        }
    }
}
