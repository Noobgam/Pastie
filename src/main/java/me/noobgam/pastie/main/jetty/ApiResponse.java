package me.noobgam.pastie.main.jetty;

public class ApiResponse<T> extends RequestResponse {

    private final T result;

    public ApiResponse(T result) {
        super();
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
