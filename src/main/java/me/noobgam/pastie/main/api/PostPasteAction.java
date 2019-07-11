package me.noobgam.pastie.main.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.noobgam.pastie.main.jetty.InvalidQueryResponse;
import me.noobgam.pastie.main.jetty.PasteResponse;
import me.noobgam.pastie.main.jetty.Utils;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.Pipeline;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.paste.Paste;
import me.noobgam.pastie.main.paste.PasteDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.stream.Collectors;

@ActionContainer("/paste")
@Pipeline(LoginAction.class)
public class PostPasteAction implements AbstractHandler2 {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PasteDao pasteDao;

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        if (!requestContext.getRequest().getMethod().equals("POST")) {
            requestContext.respond(
                    400,
                    new InvalidQueryResponse("POST method is required")
            );
            return requestContext;
        }
        Paste paste = new Paste(
                Utils.getRandomAlNum(6),
                // TODO(noobgam): you know this ain't right.
                "noobgam",
                requestContext.getRequest().getReader().lines().collect(Collectors.joining("\n"))
        );
        pasteDao.insertOne(paste).join();
        requestContext.success(new PasteResponse(paste.getId()));
        return requestContext;
    }
}
