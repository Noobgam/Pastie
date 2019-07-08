package me.noobgam.pastie.main.users;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UserMongoDao implements UserDao {

    private final MongoAsyncCollectionX<ObjectId, User> collection;

    public UserMongoDao(MongoAsyncCollectionX<ObjectId, User> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Optional<User>> findByUsername(String username) {
        return collection.findOneByFilter(Filters.eq("username", username));
    }

    @Override
    public CompletableFuture<List<User>> findAll() {
        return collection.findAll();
    }

    @Override
    public CompletableFuture<Set<String>> findAllHandles() {
        Set<String> strings = new ObjectOpenHashSet<>();
        return collection.forEach(
                MongoAsyncCollectionX.EMPTY_FILTER,
                Projections.fields(
                        Projections.excludeId(),
                        Projections.include("username")
                ),
                String.class,
                strings::add
        ).thenApply(v -> strings);
    }
}
