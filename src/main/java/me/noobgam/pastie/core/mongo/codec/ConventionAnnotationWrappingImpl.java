package me.noobgam.pastie.core.mongo.codec;


import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.lang.reflect.Modifier.isPublic;

public final class ConventionAnnotationWrappingImpl implements Convention {

    @Override
    public void apply(final ClassModelBuilder<?> classModelBuilder) {
        for (final Annotation annotation : classModelBuilder.getAnnotations()) {
            processClassAnnotation(classModelBuilder, annotation);
        }

        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            processPropertyAnnotations(classModelBuilder, propertyModelBuilder);
        }

        processCreatorAnnotation(classModelBuilder);
    }

    private void processClassAnnotation(final ClassModelBuilder<?> classModelBuilder, final Annotation annotation) {
        if (annotation instanceof BsonDiscriminator) {
            BsonDiscriminator discriminator = (BsonDiscriminator) annotation;
            String key = discriminator.key();

            classModelBuilder.discriminatorKey(key.isEmpty() ? "_t" : key);

            String name = discriminator.value();
            if (classModelBuilder.getType() != null) {
                classModelBuilder.discriminator(name.isEmpty() ? classModelBuilder.getType().getName() : name);
            }
            classModelBuilder.enableDiscriminator(true);
        }
    }

    private void processPropertyAnnotations(final ClassModelBuilder<?> classModelBuilder,
                                            final PropertyModelBuilder<?> propertyModelBuilder) {
        for (Annotation annotation : propertyModelBuilder.getReadAnnotations()) {
            if (annotation instanceof BsonProperty) {
                BsonProperty bsonProperty = (BsonProperty) annotation;
                if (!bsonProperty.value().isEmpty()) {
                    propertyModelBuilder.readName(bsonProperty.value());
                }
                propertyModelBuilder.discriminatorEnabled(bsonProperty.useDiscriminator());
                if (propertyModelBuilder.getName().equals(classModelBuilder.getIdPropertyName())) {
                    classModelBuilder.idPropertyName(null);
                }
            } else if (annotation instanceof BsonId) {
                classModelBuilder.idPropertyName(propertyModelBuilder.getName());
            } else if (annotation instanceof BsonIgnore) {
                propertyModelBuilder.readName(null);
            }
        }

        for (Annotation annotation : propertyModelBuilder.getWriteAnnotations()) {
            if (annotation instanceof BsonProperty) {
                BsonProperty bsonProperty = (BsonProperty) annotation;
                if (!bsonProperty.value().isEmpty()) {
                    propertyModelBuilder.writeName(bsonProperty.value());
                }
            } else if (annotation instanceof BsonIgnore) {
                propertyModelBuilder.writeName(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void processCreatorAnnotation(final ClassModelBuilder<T> classModelBuilder) {
        Class<T> clazz = classModelBuilder.getType();
        WrappingCreatorExecutable<T> wrappingCreatorExecutable = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!constructor.isSynthetic()) {
                for (Annotation annotation : constructor.getDeclaredAnnotations()) {
                    if (annotation.annotationType().equals(BsonCreator.class)) {
                        if (wrappingCreatorExecutable != null) {
                            throw new CodecConfigurationException("Found multiple constructors annotated with @BsonCreator");
                        }
                        if (!isPublic(constructor.getModifiers())) {
                            constructor.setAccessible(true);
                        }
                        wrappingCreatorExecutable = new WrappingCreatorExecutable<>(
                                clazz,
                                (Constructor<T>) constructor,
                                classModelBuilder
                        );
                    }
                }
            }
        }

        if (wrappingCreatorExecutable == null) {
            // try to find any public constructor if none were annotated with BsonCreator
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (isPublic(constructor.getModifiers()) && !constructor.isSynthetic()) {
                    if (wrappingCreatorExecutable != null) {
                        throw new CodecConfigurationException(
                                "None constructors are annotated with @BsonCreator, and multiple public found."
                        );
                    }
                    wrappingCreatorExecutable = new WrappingCreatorExecutable<>(
                            clazz,
                            (Constructor<T>) constructor,
                            classModelBuilder
                    );
                }
            }
        }

        if (wrappingCreatorExecutable != null) {
//            The code below is incorrect, Mongo driver passes all pojos to the convention
//            Thus the code below won't work in case nested pojo does not contain @BsonId
//
//            if (wrappingCreatorExecutable.getIdPropertyIndex() == -1) {
//                // Do not allow such POJOs here, because otherwise the only way to detect problem is
//                //  during object encoding (insertion to DB)
//                throw new IllegalArgumentException(String.format("Could not find id field for class %s.", clazz));
//            }
            classModelBuilder.instanceCreatorFactory(new WrappingInstanceCreatorFactoryImpl<>(wrappingCreatorExecutable));
            cleanPropertyBuilders(classModelBuilder);
        }
    }

    private void cleanPropertyBuilders(final ClassModelBuilder<?> classModelBuilder) {
        // This is necessary because mongo extracts all methods named getXXX (even static ones)
        // this causes weird side effects when class contains a method with signature of sorts
        // public static Pojo getNewPojo()
        // with this method mongo driver expects you to have a field named "newPojo" and will throw an exception
        // if is not present. I'm almost certain this is not a reasonable rule
        // so I cleanup every "getter" which does not have matching field
        List<String> propertiesToRemove = new ArrayList<>();
        Class<?> t = classModelBuilder.getType();
        Set<String> fields = new HashSet<>();
        while (t != null) {
            Arrays.stream(t.getDeclaredFields())
                    .filter(f -> !(Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())))
                    .map(Field::getName).forEach(fields::add);
            t = t.getSuperclass();
        }
        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {

            String propName = propertyModelBuilder.getName();
            if (!fields.contains(propName)
                    || (!propertyModelBuilder.isReadable() && !propertyModelBuilder.isWritable())
            ) {
                propertiesToRemove.add(propertyModelBuilder.getName());
            }
        }
        for (String propertyName : propertiesToRemove) {
            classModelBuilder.removeProperty(propertyName);
        }
    }
}
