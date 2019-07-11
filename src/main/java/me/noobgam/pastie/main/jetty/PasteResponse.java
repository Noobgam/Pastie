package me.noobgam.pastie.main.jetty;

public class PasteResponse extends RequestResponse {
    private final String pasteId;

    public PasteResponse(String pasteId) {
        super(true);
        this.pasteId = pasteId;
    }

    public String getPasteId() {
        return pasteId;
    }
}
