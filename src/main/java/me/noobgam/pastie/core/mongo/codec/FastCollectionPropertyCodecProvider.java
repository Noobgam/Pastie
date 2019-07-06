package me.noobgam.pastie.core.mongo.codec;

import java.util.Collection;
import java.util.HashSet;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

import static java.lang.String.format;

/**
 * @author noobgam
 */
public final class FastCollectionPropertyCodecProvider implements PropertyCodecProvider {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> Codec<T> get(final TypeWithTypeParameters<T> type, final PropertyCodecRegistry registry) {
        if (Collection.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 1) {
            Class<?> clazz = type.getTypeParameters().get(0).getType();
            if (clazz.equals(Integer.class)) {
                return (Codec<T>) new IntegerCollectionCodec((Class<Collection<Integer>>) type.getType());
            } else if (clazz.equals(Long.class)) {
                return (Codec<T>) new LongCollectionCodec((Class<Collection<Long>>) type.getType());
            } else {
                return new GenericCollectionCodec(type.getType(), registry.get(type.getTypeParameters().get(0)));
            }
        } else {
            return null;
        }
    }

    private static class IntegerCollectionCodec implements Codec<Collection<Integer>> {
        private final Class<Collection<Integer>> encoderClass;

        IntegerCollectionCodec(final Class<Collection<Integer>> encoderClass) {
            this.encoderClass = encoderClass;
        }

        @Override
        public void encode(final BsonWriter writer, final Collection<Integer> collection, final EncoderContext encoderContext) {
            writer.writeStartArray();
            for (final int value : collection) {
                writer.writeInt32(value);
            }
            writer.writeEndArray();
        }

        @Override
        public Collection<Integer> decode(final BsonReader reader, final DecoderContext context) {
            Collection<Integer> collection = getInstance();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                collection.add(reader.readInt32());
            }
            reader.readEndArray();
            return collection;
        }

        @Override
        public Class<Collection<Integer>> getEncoderClass() {
            return encoderClass;
        }

        private Collection<Integer> getInstance() {
            if (encoderClass.isInterface()) {
                if (encoderClass.isAssignableFrom(IntArrayList.class)) {
                    return new IntArrayList();
                } else if (encoderClass.isAssignableFrom(IntOpenHashSet.class)) {
                    return new IntOpenHashSet(); // debatable
                } else {
                    throw new CodecConfigurationException(format("Unsupported Collection interface of %s!", encoderClass.getName()));
                }
            }

            try {
                return encoderClass.getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                throw new CodecConfigurationException(e.getMessage(), e);
            }
        }
    }

    private static class LongCollectionCodec implements Codec<Collection<Long>> {
        private final Class<Collection<Long>> encoderClass;

        LongCollectionCodec(final Class<Collection<Long>> encoderClass) {
            this.encoderClass = encoderClass;
        }

        @Override
        public void encode(final BsonWriter writer, final Collection<Long> collection, final EncoderContext encoderContext) {
            writer.writeStartArray();
            for (final long value : collection) {
                writer.writeInt64(value);
            }
            writer.writeEndArray();
        }

        @Override
        public Collection<Long> decode(final BsonReader reader, final DecoderContext context) {
            Collection<Long> collection = getInstance();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                collection.add(reader.readInt64());
            }
            reader.readEndArray();
            return collection;
        }

        @Override
        public Class<Collection<Long>> getEncoderClass() {
            return encoderClass;
        }

        private Collection<Long> getInstance() {
            if (encoderClass.isInterface()) {
                if (encoderClass.isAssignableFrom(LongArrayList.class)) {
                    return new LongArrayList();
                } else if (encoderClass.isAssignableFrom(LongOpenHashSet.class)) {
                    return new LongOpenHashSet(); // debatable
                } else {
                    throw new CodecConfigurationException(format("Unsupported Collection interface of %s!", encoderClass.getName()));
                }
            }

            try {
                return encoderClass.getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                throw new CodecConfigurationException(e.getMessage(), e);
            }
        }
    }

    private static class GenericCollectionCodec<T> implements Codec<Collection<T>> {
        private final Class<Collection<T>> encoderClass;
        private final Codec<T> codec;

        GenericCollectionCodec(final Class<Collection<T>> encoderClass, final Codec<T> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(final BsonWriter writer, final Collection<T> collection, final EncoderContext encoderContext) {
            writer.writeStartArray();
            for (final T value : collection) {
                if (value == null) {
                    writer.writeNull();
                } else {
                    codec.encode(writer, value, encoderContext);
                }
            }
            writer.writeEndArray();
        }

        @Override
        public Collection<T> decode(final BsonReader reader, final DecoderContext context) {
            Collection<T> collection = getInstance();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    collection.add(null);
                    reader.readNull();
                } else {
                    collection.add(codec.decode(reader, context));
                }
            }
            reader.readEndArray();
            return collection;
        }

        @Override
        public Class<Collection<T>> getEncoderClass() {
            return encoderClass;
        }

        private Collection<T> getInstance() {
            if (encoderClass.isInterface()) {
                if (encoderClass.isAssignableFrom(ObjectArrayList.class)) {
                    return new ObjectArrayList<>();
                } else if (encoderClass.isAssignableFrom(ObjectOpenHashSet.class)) {
                    return new HashSet<>();
                } else {
                    throw new CodecConfigurationException(format("Unsupported Collection interface of %s!", encoderClass.getName()));
                }
            }

            try {
                return encoderClass.getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                throw new CodecConfigurationException(e.getMessage(), e);
            }
        }
    }
}
