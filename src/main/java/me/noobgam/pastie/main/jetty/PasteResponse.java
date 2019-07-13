package me.noobgam.pastie.main.jetty;

import com.fasterxml.jackson.annotation.JsonInclude;
import me.noobgam.pastie.main.paste.Paste;

import javax.annotation.Nullable;

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
}
