package me.noobgam.pastie.main.jetty;

import me.noobgam.pastie.core.env.Environment;

public class RequestResponse {
    private final String host;
    private final String requestId;
    private final boolean success;
    private long handleMs;
    private int status;

    public RequestResponse(boolean success) {
        host = Environment.HOST;
        requestId = Utils.getRandomAlNum(15);
        this.success = success;
    }

    public String getHost() {
        return host;
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getHandleMs() {
        return handleMs;
    }

    public int getStatus() {
        return status;
    }

    public void setHandleMs(long handleMs) {
        this.handleMs = handleMs;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
