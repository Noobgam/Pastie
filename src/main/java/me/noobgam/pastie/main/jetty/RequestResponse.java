package me.noobgam.pastie.main.jetty;

public class RequestResponse {
    private final String host;
    private final String requestId;

    public RequestResponse() {
        host = Environment.HOST;
        requestId = Utils.getRandomAlNum(15);
    }

    public String getHost() {
        return host;
    }

    public String getRequestId() {
        return requestId;
    }
}
