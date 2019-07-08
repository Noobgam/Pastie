package me.noobgam.pastie.main.core;

import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.main.api.*;
import me.noobgam.pastie.utils.MainSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.util.Arrays;

public class Core extends MainSupport {

    private static final Logger logger = LogManager.getLogger(Core.class);

    public static volatile boolean ready = false;

    public static void main(String[] args) {
        new Core().injectRun(args);
    }

    private ContextHandlerCollection collectHandlers(Class<?>... classes) {
        return new ContextHandlerCollection(
                Arrays.stream(classes).map(clazz -> {
                    Handler bean = (Handler) context.getBean(clazz);
                    ActionContainer annotation =
                            clazz.getAnnotation(ActionContainer.class);
                    ContextHandler contextHandler = new ContextHandler();
                    contextHandler.setContextPath(annotation.value());
                    contextHandler.setHandler(bean);
                    return contextHandler;
                }).toArray(ContextHandler[]::new)
        );
    }

    @Override
    public void run(String[] args) {
        Server server = new Server(
                PropertiesHolder.getIntProperty("core.port")
        );
        server.setHandler(
                collectHandlers(
                        PostPasteAction.class,
                        AuthAction.class,
                        PingAction.class
                )
        );
        try {
            server.start();
        } catch (Exception e) {
            logger.fatal("Jetty fail to start {}", e);
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
                ActionContainerContextConfiguration.class
        };
    }
}
