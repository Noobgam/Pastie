package me.noobgam.pastie.main.jetty.dto;

public class SuccessResponse extends RequestResponse {

    public final String result;

    private SuccessResponse(boolean success, String result) {
        super(success);
        this.result = result;
    }

    public static SuccessResponse fail() {
        return new SuccessResponse(false, "error");
    }

    public static SuccessResponse notReady() {
        return new SuccessResponse(false, "not ready");
    }

    public static SuccessResponse pong() {
        return new SuccessResponse(true, "pong");
    }

    public static SuccessResponse success() {
        return new SuccessResponse(true, "success");
    }
}
