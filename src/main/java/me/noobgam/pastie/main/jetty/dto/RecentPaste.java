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
        int cnt = 0;
        for (int i = 0; i < paste.getContent().length(); ++i) {
            if (paste.getContent().charAt(i) == '\n') {
                ++cnt;
            }
            if (cnt == 5) {
                return paste.getContent().substring(0, i);
            }
        }
        return paste.getContent();
    }

    public String getId() {
        return paste.getId();
    }

    public String getOwner() {
        return paste.getOwner();
    }

    public String getLang() {
        return paste.getLanguage();
    }

    @Nullable
    public Long getIsoMillis() {
        Instant instant = paste.getInstant();
        return instant != null
                ? instant.toEpochMilli()
                : null;
    }

    @Nullable
    public Long getUtc() {
        Instant instant = paste.getInstant();
        return instant == null ? null : instant.getEpochSecond();
    }
}
