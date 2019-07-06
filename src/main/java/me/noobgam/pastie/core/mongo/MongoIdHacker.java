package me.noobgam.pastie.core.mongo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 * @author noobgam
 */
public class MongoIdHacker<TId, TEntity> {
    private final Field field;

    private MongoIdHacker(Field field) {
        this.field = field;
    }

    @SuppressWarnings("unchecked")
    public TId getId(TEntity entity) {
        try {
            return (TId) field.get(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <TId, TEntity> MongoIdHacker<TId, TEntity> cons(Class<TEntity> entityClass) {
        List<Field> fields = Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> f.getAnnotationsByType(BsonIgnore.class).length == 0)
                .filter(f -> f.getAnnotationsByType(BsonId.class).length > 0)
                .collect(Collectors.toList());
        if (fields.isEmpty()) {
            throw new RuntimeException("Id field not found.");
        }
        if (fields.size() > 1) {
            throw new RuntimeException("Conflicting fields in class, multiple BsonId defined");
        }
        Field field = fields.get(0);
        field.setAccessible(true);
        return new MongoIdHacker<>(field);
    }
}
