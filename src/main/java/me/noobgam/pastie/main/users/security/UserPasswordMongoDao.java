package me.noobgam.pastie.main.users.security;

import com.mongodb.async.client.ClientSession;
import com.mongodb.client.model.Filters;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserPasswordMongoDao implements UserPasswordDao {
    private final MongoAsyncCollectionX<ObjectId, UserPassword> collection;

    public UserPasswordMongoDao(MongoAsyncCollectionX<ObjectId, UserPassword> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Optional<UserPassword>> findByIdAndPassword(
            ObjectId id,
            byte[] password
    ) {
        return collection.findOneByFilter(
                Filters.and(
                        Filters.eq("_id", id),
                        Filters.eq("password", password)
                )
        );
    }

    public CompletableFuture<Void> createCredentials(
            ObjectId objectId,
            byte[] userPassword,
            ClientSession clientSession
    ) {
        return collection.insertOne(new UserPassword(objectId, userPassword), clientSession);
    }
}
