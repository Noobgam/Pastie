package me.noobgam.pastie.main.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.noobgam.pastie.main.jetty.InvalidQueryResponse;
import me.noobgam.pastie.main.jetty.SuccessResponse;
import me.noobgam.pastie.main.jetty.Utils;
import me.noobgam.pastie.main.jetty.helpers.ErrorAutoHandler;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.paste.Paste;
import me.noobgam.pastie.main.paste.PasteDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.stream.Collectors;

@ActionContainer("/paste")
public class PostPasteAction extends ErrorAutoHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PasteDao pasteDao;

    @Override
    public void handle2(RequestContext requestContext) throws IOException, ServletException {
        if (!requestContext.getRequest().getMethod().equals("POST")) {
            requestContext.respond(
                    400,
                    new InvalidQueryResponse("POST method is required")
            );
            return;
        }
        Paste paste = new Paste(
                Utils.getRandomAlNum(6),
                // TODO(noobgam): you know this ain't right.
                "noobgam",
                requestContext.getRequest().getReader().lines().collect(Collectors.joining("\n"))
        );
        pasteDao.insertOne(paste).join();
        requestContext.success(SuccessResponse.success());
    }
}
