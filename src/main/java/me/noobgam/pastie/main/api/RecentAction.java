package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.dto.ExceptionResponse;
import me.noobgam.pastie.main.jetty.dto.InvalidQueryResponse;
import me.noobgam.pastie.main.jetty.dto.RecentResponse;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.paste.Paste;
import me.noobgam.pastie.main.paste.PasteCache;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ActionContainer("/recent")
public class RecentAction implements AbstractHandler2 {

    private final PasteCache pasteCache;

    public RecentAction(PasteCache pasteCache) {
        this.pasteCache = pasteCache;
    }

    @Override
    public RequestContext handle(RequestContext requestContext) throws IOException, ServletException {
        Map<String, String> params = requestContext.getUrlParams();
        String handle = params.get("handle");
        if (handle == null) {
            requestContext.respond(
                    400,
                    new InvalidQueryResponse("Handle is required")
            );
            return requestContext;
        }
        Optional<Map<String, Paste>> idToPaste = pasteCache.cachedOrCurrent();
        if (idToPaste.isEmpty()) {
            requestContext.respond(
                    500,
                    new ExceptionResponse(new IllegalStateException("Cache value could not be calculated"))
            );
            return requestContext;
        }
        List<Paste> pastes =
                idToPaste.get()
                        .values()
                        .stream()
                        .filter(paste -> paste.getOwner().equals(handle))
                        .collect(Collectors.toList());
        requestContext.success(new RecentResponse(pastes));
        return requestContext;
    }
}
