package me.noobgam.pastie.main.core;

import io.prometheus.client.exporter.HTTPServer;
import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.main.api.*;
import me.noobgam.pastie.main.background.SchedulerContextConfiguration;
import me.noobgam.pastie.main.jetty.helpers.AbstractHandler2;
import me.noobgam.pastie.main.jetty.helpers.ActionContainer;
import me.noobgam.pastie.main.jetty.helpers.Pipeline;
import me.noobgam.pastie.main.jetty.helpers.PipelineHandler;
import me.noobgam.pastie.utils.MainSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class Core extends MainSupport {

    private static final Logger logger = LogManager.getLogger(Core.class);

    public static void main(String[] args) {
        new Core().injectRun(args);
    }

    private Class<?>[] classPipeline(Class<?>... classes) {
        return classes;
    }

    private Class<?>[] classPipeline(Class<?>[] classes, Class<?> clazz) {
        return Stream.concat(Stream.of(classes), Stream.of(clazz)).toArray(Class<?>[]::new);
    }

    private ContextHandlerCollection collectHandlers(
            Collection<Class<?>> classes,
            ContextHandler... extraHandlers
    ) {
        return new ContextHandlerCollection(
                Stream.concat(classes.stream().map(clazz -> {
                    ActionContainer containerAnnotation =
                            clazz.getAnnotation(ActionContainer.class);
                    Pipeline pipelineAnnotation = clazz.getAnnotation(Pipeline.class);
                    Class<?>[] pipeline =
                            pipelineAnnotation == null
                                    ? classPipeline(clazz)
                                    : classPipeline(pipelineAnnotation.value(), clazz);
                    ContextHandler contextHandler = new ContextHandler();
                    contextHandler.setContextPath("/api" + containerAnnotation.value());
                    contextHandler.setHandler(new PipelineHandler(
                            containerAnnotation.handleErrors(),
                            Stream.of(pipeline)
                                    .map(clz -> (AbstractHandler2) context.getBean(clz))
                                    .toArray(AbstractHandler2[]::new)
                    ));
                    return contextHandler;
                }), Arrays.stream(extraHandlers)).toArray(ContextHandler[]::new)
        );
    }

    @Override
    public void run(String[] args) {
        Server server = new Server(
                PropertiesHolder.getIntProperty("core.port")
        );
        server.setHandler(
                collectHandlers(
                        Arrays.asList(
                                PostPasteAction.class,
                                LoginAction.class,
                                LogoutAction.class,
                                PingAction.class,
                                RegisterAction.class,
                                GetPasteAction.class
                        )
                )
        );
        final HTTPServer httpServer;
        try {
            server.start();
            httpServer = new HTTPServer(
                    PropertiesHolder.getIntProperty("prometheus.port")
            );
        } catch (Exception e) {
            logger.fatal("Http server failed to start", e);
            return;
        }
        try {
            server.join();
        } catch (InterruptedException ex) {
            logger.info("Jetty interrupted", ex);
        }
    }

    @Override
    public Class<?>[] getApplicationContexts() {
        return new Class<?>[]{
                ActionContainerContextConfiguration.class,
                SchedulerContextConfiguration.class
        };
    }
}
