package me.noobgam.pastie.core.mongo.codec;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

/**
 * Usage of such maps is highly discouraged.
 * @author noobgam
 */
public class NumberMapPropertyCodecProvider implements PropertyCodecProvider {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> Codec<T> get(final TypeWithTypeParameters<T> type, final PropertyCodecRegistry registry) {
        if (Map.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 2) {
            Class<?> keyType = type.getTypeParameters().get(0).getType();
            Codec<?> valueCodec = registry.get(type.getTypeParameters().get(1));
            // I'm quite hesitant to do this via reflection due to performance issues.
            //  neither this is necessary to support generic number class.
            //  also I refuse to support Double/Float values as keys in fields.
            if (keyType.equals(Integer.class)) {
                return new IntegerMapCodec(type.getType(), valueCodec);
            } else if (keyType.equals(Long.class)) {
                return new LongMapCodec(type.getType(), valueCodec);
            } else if (keyType.equals(BigInteger.class)) {
                return new BigIntegerMapCodec(type.getType(), valueCodec);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    static class IntegerMapCodec<V> implements Codec<Map<Integer, V>> {
        private final Class<Map<Integer, V>> encoderClass;
        private final Codec<V> codec;

        IntegerMapCodec(final Class<Map<Integer, V>> encoderClass, final Codec<V> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(final BsonWriter writer, final Map<Integer, V> map, final EncoderContext encoderContext) {
            writer.writeStartDocument();
            for (final Map.Entry<Integer, V> entry : map.entrySet()) {
                writer.writeName(entry.getKey().toString());
                if (entry.getValue() == null) {
                    writer.writeNull();
                } else {
                    codec.encode(writer, entry.getValue(), encoderContext);
                }
            }
            writer.writeEndDocument();
        }

        @Override
        public Map<Integer, V> decode(final BsonReader reader, final DecoderContext context) {
            reader.readStartDocument();
            Map<Integer, V> map = new HashMap<>();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    map.put(Integer.parseInt(reader.readName()), null);
                    reader.readNull();
                } else {
                    map.put(Integer.parseInt(reader.readName()), codec.decode(reader, context));
                }
            }
            reader.readEndDocument();
            return map;
        }

        @Override
        public Class<Map<Integer, V>> getEncoderClass() {
            return encoderClass;
        }
    }

    static class LongMapCodec<V> implements Codec<Map<Long, V>> {
        private final Class<Map<Long, V>> encoderClass;
        private final Codec<V> codec;

        LongMapCodec(final Class<Map<Long, V>> encoderClass, final Codec<V> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(final BsonWriter writer, final Map<Long, V> map, final EncoderContext encoderContext) {
            writer.writeStartDocument();
            for (final Map.Entry<Long, V> entry : map.entrySet()) {
                writer.writeName(entry.getKey().toString());
                if (entry.getValue() == null) {
                    writer.writeNull();
                } else {
                    codec.encode(writer, entry.getValue(), encoderContext);
                }
            }
            writer.writeEndDocument();
        }

        @Override
        public Map<Long, V> decode(final BsonReader reader, final DecoderContext context) {
            reader.readStartDocument();
            Map<Long, V> map = new HashMap<>();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    map.put(Long.parseLong(reader.readName()), null);
                    reader.readNull();
                } else {
                    map.put(Long.parseLong(reader.readName()), codec.decode(reader, context));
                }
            }
            reader.readEndDocument();
            return map;
        }

        @Override
        public Class<Map<Long, V>> getEncoderClass() {
            return encoderClass;
        }
    }

    static class BigIntegerMapCodec<V> implements Codec<Map<BigInteger, V>> {
        private final Class<Map<BigInteger, V>> encoderClass;
        private final Codec<V> codec;

        BigIntegerMapCodec(final Class<Map<BigInteger, V>> encoderClass, final Codec<V> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(final BsonWriter writer, final Map<BigInteger, V> map, final EncoderContext encoderContext) {
            writer.writeStartDocument();
            for (final Map.Entry<BigInteger, V> entry : map.entrySet()) {
                writer.writeName(entry.getKey().toString());
                if (entry.getValue() == null) {
                    writer.writeNull();
                } else {
                    codec.encode(writer, entry.getValue(), encoderContext);
                }
            }
            writer.writeEndDocument();
        }

        @Override
        public Map<BigInteger, V> decode(final BsonReader reader, final DecoderContext context) {
            reader.readStartDocument();
            Map<BigInteger, V> map = new HashMap<>();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    map.put(new BigInteger(reader.readName()), null);
                    reader.readNull();
                } else {
                    map.put(new BigInteger(reader.readName()), codec.decode(reader, context));
                }
            }
            reader.readEndDocument();
            return map;
        }

        @Override
        public Class<Map<BigInteger, V>> getEncoderClass() {
            return encoderClass;
        }
    }
}
