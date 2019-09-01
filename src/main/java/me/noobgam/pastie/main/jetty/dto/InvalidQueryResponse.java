package me.noobgam.pastie.main.jetty.dto;

public class InvalidQueryResponse extends RequestResponse {
    public String error;

    public InvalidQueryResponse(String error) {
        super(false);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
