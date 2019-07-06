package me.noobgam.pastie.core.mongo.codec;

import java.util.Map;

import org.bson.codecs.pojo.InstanceCreator;
import org.bson.codecs.pojo.PropertyModel;

/**
 * @author noobgam
 */
public final class WrappingInstanceCreatorImpl<T> implements InstanceCreator<T> {
    private static final String ID_FIELD = "_id";

    private final WrappingCreatorExecutable<T> wrappingCreatorExecutable;
    private final Map<String, Integer> properties;
    private final Object[] params;

    private T newInstance;

    WrappingInstanceCreatorImpl(final WrappingCreatorExecutable<T> wrappingCreatorExecutable) {
        this.wrappingCreatorExecutable = wrappingCreatorExecutable;
        if (wrappingCreatorExecutable.getProperties().isEmpty()) {
            this.properties = null;
            this.params = null;
            this.newInstance = wrappingCreatorExecutable.getInstance();
        } else {
            this.properties = wrappingCreatorExecutable.cloneProperties();
            this.params = new Object[properties.size()];
            this.newInstance = null;
        }
    }

    @Override
    public <S> void set(final S value, final PropertyModel<S> propertyModel) {
        if (!properties.isEmpty()) {
            String propertyName = propertyModel.getWriteName();
            Integer index = properties.get(propertyName);
            if (index != null) {
                params[index] = value;
            }
            properties.remove(propertyName);
        }
        if (properties.isEmpty()) {
            newInstance = wrappingCreatorExecutable.getInstance(params);
        }
    }

    @Override
    public T getInstance() {
        if (newInstance == null) {
            newInstance = wrappingCreatorExecutable.getInstance(params);
        }
        return newInstance;
    }
}
