package me.noobgam.pastie.main.mongo;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;

public final class MongoUtils {
    private MongoUtils() {

    }

    public static <TId, TEntity> MongoAsyncCollectionX<TId, TEntity> getAsyncCollectionX(
            MongoClient client,
            String database,
            String collection,
            Class<TEntity> clazz
    ) {
        MongoCollection<TEntity> coll =
                client.getDatabase(database).getCollection(collection, clazz);
        return new MongoAsyncCollectionX<>(
                coll, clazz
        );
    }
}
