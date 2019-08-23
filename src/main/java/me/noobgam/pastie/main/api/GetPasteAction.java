package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.InvalidQueryResponse;
import me.noobgam.pastie.main.jetty.PasteResponse;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.Pipeline;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.jetty.helpers.handlers.AuthAction;
import me.noobgam.pastie.main.paste.Paste;
import me.noobgam.pastie.main.paste.PasteDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;

@ActionContainer("/getpaste")
@Pipeline(AuthAction.class)
public class GetPasteAction implements AbstractHandler2 {

    @Autowired
    private PasteDao pasteDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        if (!requestContext.getRequest().getMethod().equals("GET")) {
            requestContext.respond(
                    400,
                    new InvalidQueryResponse("GET method is required")
            );
            return requestContext;
        }
        String id = requestContext.getUrlParams().get("id");
        if (id == null) {
            requestContext.respond(
                    400,
                    new InvalidQueryResponse("ID parameter is required")
            );
            return requestContext;
        }
        Optional<Paste> pasteO = pasteDao.findById(id).join();
        if (pasteO.isEmpty()) {
            requestContext.respond(
                    404,
                    new InvalidQueryResponse("Paste not found")
            );
            return requestContext;
        }
        boolean raw =
                Optional.ofNullable(requestContext.getUrlParams().get("raw"))
                        .map(Boolean::valueOf)
                        .orElse(false);
        if (!raw) {
            requestContext.success(new PasteResponse(pasteO.get()));
        } else {
            requestContext.respondPlainText(200, pasteO.get().getContent());
        }
        return requestContext;
    }
}
