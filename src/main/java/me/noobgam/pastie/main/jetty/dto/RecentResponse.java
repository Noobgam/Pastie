package me.noobgam.pastie.main.jetty.dto;

import me.noobgam.pastie.main.paste.Paste;

import java.util.ArrayList;
import java.util.List;

public class RecentResponse extends RequestResponse {
    private List<RecentPaste> pastes;

    public RecentResponse(List<Paste> pastes) {
        super(true);

        // jackson is bad. java.lang is not any better.
        // I'm not using iterable here, neither I'm going to pass stream to constructor.
        ArrayList<RecentPaste> pasteResponses = new ArrayList<>(pastes.size());
        for (Paste paste : pastes) {
            pasteResponses.add(new RecentPaste(paste));
        }
        this.pastes = pasteResponses;
    }

    public List<RecentPaste> getPastes() {
        return pastes;
    }
}
