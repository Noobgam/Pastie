package me.noobgam.pastie.main.jetty.dto;

import me.noobgam.pastie.main.paste.Paste;

import java.util.ArrayList;
import java.util.List;

public class RecentResponse extends RequestResponse {
    private List<PasteResponse> pastes;

    public RecentResponse(List<Paste> pastes) {
        super(true);
        ArrayList<PasteResponse> pasteResponses = new ArrayList<>(pastes.size());
        for (Paste paste : pastes) {
            pasteResponses.add(new PasteResponse(paste));
        }
        this.pastes = pasteResponses;
    }

    public List<PasteResponse> getPastes() {
        return pastes;
    }
}
