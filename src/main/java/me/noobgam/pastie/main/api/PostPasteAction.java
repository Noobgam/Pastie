package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.Utils;
import me.noobgam.pastie.main.jetty.dto.InvalidQueryResponse;
import me.noobgam.pastie.main.jetty.dto.PostPasteResponse;
import me.noobgam.pastie.main.jetty.dto.SuccessResponse;
import me.noobgam.pastie.main.jetty.helpers.*;
import me.noobgam.pastie.main.jetty.helpers.handlers.AuthAction;
import me.noobgam.pastie.main.paste.Paste;
import me.noobgam.pastie.main.paste.PasteDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.stream.Collectors;

@ActionContainer("/paste")
@Pipeline(AuthAction.class)
public class PostPasteAction implements AbstractHandler2 {

    @Autowired
    private PasteDao pasteDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        if (requestContext.getRequest().getMethod().equals("OPTIONS")) {
            requestContext.respond(
                    200,
                    SuccessResponse.success()
            );
            return requestContext;
        }
        if (!requestContext.getRequest().getMethod().equals("POST")) {
            requestContext.respond(
                    400,
                    new InvalidQueryResponse("POST method is required")
            );
            return requestContext;
        }
        AuthenticatedRequestContextHolder contextHolder = (AuthenticatedRequestContextHolder) requestContext;
        Paste paste = Paste.cons(
                Utils.getRandomAlNum(6),
                contextHolder.getUser().getUsername(),
                requestContext.getRequest().getReader().lines().collect(Collectors.joining("\n")),
                requestContext.getHeader("X-Paste-Lang")
        );
        pasteDao.insertOne(paste).join();
        requestContext.success(new PostPasteResponse(paste.getId()));
        return requestContext;
    }
}
