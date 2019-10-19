package me.noobgam.pastie.main.jetty.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import me.noobgam.pastie.main.paste.Paste;

import javax.annotation.Nullable;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasteResponse extends RequestResponse {

    private final Paste paste;

    public PasteResponse(Paste paste) {
        super(true);
        this.paste = paste;
    }

    public String getContent() {
        return paste.getContent();
    }

    public String getOwner() {
        return paste.getOwner();
    }

    @Nullable
    public String getLang() {
        return paste.getLanguage();
    }

    @Nullable
    public Long getInstant() {
        Instant instant = paste.getInstant();
        return instant != null
                ? instant.toEpochMilli()
                : null;
    }
}
