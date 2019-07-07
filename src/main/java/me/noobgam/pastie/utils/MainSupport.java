package me.noobgam.pastie.utils;

import me.noobgam.pastie.core.env.Environment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class MainSupport {
    protected ApplicationContext context;

    protected void injectRun(String[] args) {
        Environment.init();
        context = new AnnotationConfigApplicationContext(getApplicationContexts());
        run(args);
    }

    public abstract void run(String[] args);

    /**
     * usually you'd want to override this.
     *
     * @return list of contexts to build.
     */
    public Class<?>[] getApplicationContexts() {
        return new Class[0];
    }
}
