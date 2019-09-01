package me.noobgam.pastie.main.jetty.dto;

public class PostPasteResponse extends RequestResponse {
    private final String pasteId;

    public PostPasteResponse(String pasteId) {
        super(true);
        this.pasteId = pasteId;
    }

    public String getPasteId() {
        return pasteId;
    }
}
