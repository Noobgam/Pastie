package me.noobgam.pastie.core.mongo.codec;

/**
 * @author noobgam
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

final class WrappingCreatorExecutable<T> {
    private static final String ID_FIELD = "_id";

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final HashMap<String, Integer> properties;
    private final int idPropertyIndex;

    private final int[] optionalIds;
    private final int[] listIds;
    private final int[] mapIds;
    private final int[] setIds;

    WrappingCreatorExecutable(
            final Class<T> clazz,
            final Constructor<T> constructor,
            final ClassModelBuilder<T> classModelBuilder
    ) {
        Objects.requireNonNull(constructor);
        this.clazz = clazz;
        this.constructor = constructor;

        int idPropertyIndex = -1;
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Parameter[] parameters = constructor.getParameters();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        IntArrayList optionalIds = new IntArrayList();
        IntArrayList mapIds = new IntArrayList();
        IntArrayList setIds = new IntArrayList();
        IntArrayList listIds = new IntArrayList();
        ArrayList<String> properties = new ArrayList<>();

        for (int i = 0; i < parameterAnnotations.length; ++i) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            Class<?> paramType = paramTypes[i];
            if (paramType.isAssignableFrom(Optional.class)) {
                optionalIds.add(i);
            } else if (paramType.isAssignableFrom(Set.class)) {
                setIds.add(i);
            } else if (paramType.isAssignableFrom(Map.class)) {
                mapIds.add(i);
            } else if (paramType.isAssignableFrom(List.class)) {
                listIds.add(i);
            }

            boolean added = false;
            if (ID_FIELD.equals(parameters[i].getName())) {
                properties.add(null);
                idPropertyIndex = i;
                continue;
            }
            for (Annotation annotation : parameterAnnotation) {
                if (annotation.annotationType().equals(BsonId.class)) {
                    properties.add(null);
                    added = true;
                    idPropertyIndex = i;
                    break;
                }

                if (annotation.annotationType().equals(BsonProperty.class)) {
                    String propValue = ((BsonProperty)annotation).value();
                    if (ID_FIELD.equals(propValue)) {
                        properties.add(null);
                        idPropertyIndex = i;
                        added = true;
                        break;
                    }
                    properties.add(propValue);
                    added = true;
                    break;
                }
            }
            if (!added) {
                String propName = parameters[i].getName();
                // this might seem odd and redundant at first, but actually this improves
                // HashMap.get() performance, because String.equals is not getting called.
                PropertyModelBuilder propertyModelBuilder = classModelBuilder.getProperty(propName);
                if (propertyModelBuilder != null) {
                    properties.add(propertyModelBuilder.getWriteName());
                } else {
                    properties.add(parameters[i].getName());
                }
            }
        }
        this.idPropertyIndex = idPropertyIndex;
        // just making sure there's as little overhead as possible
        // there's no specific reason not to trim them

        this.properties = new HashMap<>();
        for (int i = 0; i < properties.size(); ++i) {
            if (i == idPropertyIndex) {
                this.properties.put(ID_FIELD, i);
            } else {
                this.properties.put(properties.get(i), i);
            }
        }
        this.optionalIds = toIntArray(optionalIds);
        this.setIds = toIntArray(setIds);
        this.mapIds = toIntArray(mapIds);
        this.listIds = toIntArray(listIds);
    }

    private int[] toIntArray(List<Integer> list) {
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            res[i] = list.get(i);
        }
        return res;
    }

    Class<T> getType() {
        return clazz;
    }

    protected Map<String, Integer> getProperties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    Map<String, Integer> cloneProperties() {
        return (Map<String,Integer>)properties.clone();
    }

    int getIdPropertyIndex() {
        return idPropertyIndex;
    }

    T getInstance() {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            throw new CodecConfigurationException(e.getMessage(), e);
        }
    }

    T getInstance(final Object[] params) {
        try {
            for (int optionalId : optionalIds) {
                if (params[optionalId] == null) {
                    params[optionalId] = Optional.empty();
                }
            }
            for (int mapId : mapIds) {
                if (params[mapId] == null) {
                    params[mapId] = Collections.EMPTY_MAP;
                }
            }
            for (int listId : listIds) {
                if (params[listId] == null) {
                    params[listId] = Collections.EMPTY_LIST;
                }
            }
            for (int setId : setIds) {
                if (params[setId] == null) {
                    params[setId] = Collections.EMPTY_SET;
                }
            }
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new CodecConfigurationException(e.getMessage(), e);
        }
    }
}
