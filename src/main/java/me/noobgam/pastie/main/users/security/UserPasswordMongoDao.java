package me.noobgam.pastie.main.users.security;

import com.mongodb.async.client.ClientSession;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import org.bson.types.ObjectId;

import java.util.concurrent.CompletableFuture;

public class UserPasswordMongoDao implements UserPasswordDao {
    private static final CountOptions LIMIT1 = new CountOptions().limit(1);

    private final MongoAsyncCollectionX<ObjectId, UserPassword> collection;

    public UserPasswordMongoDao(MongoAsyncCollectionX<ObjectId, UserPassword> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Boolean> validateCredentialPair(
            ObjectId id,
            byte[] password
    ) {
        return collection.count(
                Filters.and(
                        Filters.eq("_id", id),
                        Filters.eq("password", password)
                ),
                LIMIT1
        ).thenApply(x -> x > 0);
    }

    public CompletableFuture<Void> createCredentials(
            ObjectId objectId,
            byte[] userPassword,
            ClientSession clientSession
    ) {
        return collection.insertOne(new UserPassword(objectId, userPassword), clientSession);
    }
}
