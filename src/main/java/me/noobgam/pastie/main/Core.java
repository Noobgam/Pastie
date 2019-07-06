package me.noobgam.pastie.main;

import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.main.jetty.PingHandler;
import me.noobgam.pastie.main.paste.PasteDaoContextConfiguration;
import me.noobgam.pastie.utils.MainSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class Core extends MainSupport {

    private static final Logger logger = LogManager.getLogger(Core.class);

    public static volatile boolean ready = false;

    public static void main(String[] args) {
        new Core().injectRun(args);
    }

    @Override
    public void run(String[] args) {
        Server server = new Server(
                PropertiesHolder.getIntProperty("core.port")
        );
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath("/ping");
        contextHandler.setHandler(new PingHandler(() -> ready));

        ContextHandlerCollection handlerColl = new ContextHandlerCollection();
        handlerColl.setHandlers(new Handler[]{contextHandler});
        server.setHandler(handlerColl);
        try {
            server.start();
        } catch (Exception e) {
            logger.fatal("Jetty failed to start {}", e);
            return;
        }
        try {
            server.join();
        } catch (InterruptedException ex) {
            logger.info("Jetty interrupted: {}", ex);
        }
    }

    @Override
    public Class<?>[] getApplicationContexts() {
        return new Class<?>[]{
                PasteDaoContextConfiguration.class
        };
    }
}
