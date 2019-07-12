package me.noobgam.pastie.main.jetty;

import me.noobgam.pastie.main.paste.Paste;

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
}
