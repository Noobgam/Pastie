package me.noobgam.pastie.main.background;

import java.time.Duration;

public abstract class OnetimeJob extends Job {

    @Override
    protected Duration delay() {
        return null;
    }
}
