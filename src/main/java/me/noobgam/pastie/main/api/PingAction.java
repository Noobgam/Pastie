package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.SuccessResponse;
import me.noobgam.pastie.main.jetty.helpers.ErrorAutoHandler;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.function.Supplier;

@ActionContainer("/ping")
public class PingAction extends ErrorAutoHandler {

    private final Supplier<Boolean> isReadyF;

    public PingAction(Supplier<Boolean> isReadyF) {
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
