package me.noobgam.pastie.main.jetty;

public class ExceptionResponse extends RequestResponse {
    public final Throwable throwable;

    public ExceptionResponse(Throwable throwable) {
        super(false);
        this.throwable = throwable;
    }

    public String getError() {
        return throwable.getMessage();
    }
}
