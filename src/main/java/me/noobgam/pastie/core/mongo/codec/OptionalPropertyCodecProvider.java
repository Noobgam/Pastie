package me.noobgam.pastie.core.mongo.codec;

import java.util.Optional;

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
 * @author noobgam
 */
public class OptionalPropertyCodecProvider implements PropertyCodecProvider {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Codec<T> get(final TypeWithTypeParameters<T> type, final PropertyCodecRegistry registry) {
        if (Optional.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 1) {
            Codec<?> valueCodec = registry.get(type.getTypeParameters().get(0));
            return new OptionalCodec(type.getType(), valueCodec);
        } else {
            return null;
        }
    }

    static final class OptionalCodec<T> implements Codec<Optional<T>> {
        private final Class<Optional<T>> encoderClass;
        private final Codec<T> codec;

        OptionalCodec(final Class<Optional<T>> encoderClass, final Codec<T> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(final BsonWriter writer, final Optional<T> optionalValue, final EncoderContext encoderContext) {
            if (optionalValue.isPresent()) {
                optionalValue.ifPresent(val -> codec.encode(writer, val, encoderContext));
            } else {
                writer.writeNull();
            }
        }

        @Override
        public Optional<T> decode(final BsonReader reader, final DecoderContext context) {
            if (reader.getCurrentBsonType() == BsonType.NULL) {
                reader.readNull();
                return Optional.empty();
            } else {
                return Optional.of(codec.decode(reader, context));
            }
        }

        @Override
        public Class<Optional<T>> getEncoderClass() {
            return encoderClass;
        }
    }
}
