package me.noobgam.pastie.main.jetty.helpers;

import javax.servlet.ServletException;
import java.io.IOException;

public interface AbstractHandler2 {
    RequestContext handle(RequestContext requestContext) throws IOException, ServletException;
}
