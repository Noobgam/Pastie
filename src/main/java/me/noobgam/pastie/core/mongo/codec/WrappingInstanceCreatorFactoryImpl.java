package me.noobgam.pastie.core.mongo.codec;

import org.bson.codecs.pojo.InstanceCreator;
import org.bson.codecs.pojo.InstanceCreatorFactory;

/**
 * @author noobgam
 */
public final class WrappingInstanceCreatorFactoryImpl<T> implements InstanceCreatorFactory<T> {
    private final WrappingCreatorExecutable<T> wrappingCreatorExecutable;

    WrappingInstanceCreatorFactoryImpl(final WrappingCreatorExecutable<T> wrappingCreatorExecutable) {
        this.wrappingCreatorExecutable = wrappingCreatorExecutable;
    }

    @Override
    public InstanceCreator<T> create() {
        return new WrappingInstanceCreatorImpl<T>(wrappingCreatorExecutable);
    }
}
