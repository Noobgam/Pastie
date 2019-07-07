package me.noobgam.pastie.main.jetty;

import me.noobgam.pastie.main.jetty.helpers.ErrorAutoHandler;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.function.Supplier;

public class PingHandler extends ErrorAutoHandler {

    private final Supplier<Boolean> isReadyF;

    public PingHandler(Supplier<Boolean> isReadyF) {
        this.isReadyF = isReadyF;
    }

    @Override
    public void handle2(RequestContext requestContext) throws IOException, ServletException {
        boolean ready = isReadyF.get();
        if (ready) {
            requestContext.success(SuccessResponse.pong());
        } else {
            requestContext.respond(409, SuccessResponse.pong());
        }
    }
}
