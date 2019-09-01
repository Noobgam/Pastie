package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.dto.SuccessResponse;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;

import java.util.function.Supplier;

@ActionContainer("/ping")
public class PingAction implements AbstractHandler2 {

    private final Supplier<Boolean> isReadyF;

    public PingAction(Supplier<Boolean> isReadyF) {
        this.isReadyF = isReadyF;
    }

    @Override
    public RequestContext handle(RequestContext requestContext) {
        boolean ready = isReadyF.get();
        if (ready) {
            requestContext.success(SuccessResponse.pong());
        } else {
            requestContext.respond(409, SuccessResponse.pong());
        }
        return requestContext;
    }
}
