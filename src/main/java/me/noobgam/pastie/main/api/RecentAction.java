package me.noobgam.pastie.main.api;

import me.noobgam.pastie.main.jetty.dto.ExceptionResponse;
import me.noobgam.pastie.main.jetty.dto.RecentResponse;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.RequestContext;
import me.noobgam.pastie.main.paste.Paste;
import me.noobgam.pastie.main.paste.PasteCache;

import javax.servlet.ServletException;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Optional<Map<String, Paste>> idToPaste = pasteCache.cachedOrCurrent();
        if (idToPaste.isEmpty()) {
            requestContext.respond(
                    500,
                    new ExceptionResponse(new IllegalStateException("Cache value could not be calculated"))
            );
            return requestContext;
        }
        Stream<Paste> pasteStream = idToPaste.get()
                .values()
                .stream();
        if (handle != null) {
            pasteStream = pasteStream.filter(paste -> paste.getOwner().equals(handle));
        }
        final List<Paste> pastes =
                pasteStream
                        // reversed
                        .sorted((r, l) -> {
                            Instant lef = l.getInstant();
                            Instant rig = r.getInstant();
                            if (lef == null && rig == null) {
                                return l.getId().compareTo(r.getId());
                            }
                            if (lef == null || rig == null) {
                                return lef == null ? -1 : 1;
                            }
                            return lef.compareTo(rig);
                        })
                        .collect(Collectors.toList());
        requestContext.success(new RecentResponse(pastes));
        return requestContext;
    }
}
