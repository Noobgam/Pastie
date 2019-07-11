package me.noobgam.pastie.main.jetty.helpers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActionContainer {
    String value();

    boolean handleErrors() default true;
}
