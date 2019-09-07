package me.noobgam.pastie.main.jetty.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import me.noobgam.pastie.main.paste.Paste;

import javax.annotation.Nullable;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecentPaste {
    private final Paste paste;

    public RecentPaste(Paste paste) {
        this.paste = paste;
    }

    public String getSnippet() {
        // XD?
        return paste.getContent();
    }

    public String getId() {
        return paste.getId();
    }

    @Nullable
    public Long getUtc() {
        Instant instant = paste.getInstant();
        return instant == null ? null : instant.getEpochSecond();
    }
}
