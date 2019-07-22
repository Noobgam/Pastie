package me.noobgam.pastie.main.users.cookies;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CookieMongoDao implements CookieDao {

    private final MongoAsyncCollectionX<String, Cookie> collection;

    public CookieMongoDao(MongoAsyncCollectionX<String, Cookie> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Optional<Cookie>> findByCookie(@Nonnull String cookie) {
        return collection.findById(cookie);
    }

    @Override
    public CompletableFuture<DeleteResult> deAuthUser(ObjectId userId) {
        return collection.deleteMany(Filters.eq("userId", userId));
    }

    @Override
    public CompletableFuture<List<Cookie>> findAll() {
        return collection.findAll();
    }

    @Override
    public CompletableFuture<Void> storeCookie(ObjectId userId, javax.servlet.http.Cookie cookie) {
        return collection.insertOne(
                new Cookie(
                        cookie.getValue(),
                        userId,
                        cookie.getMaxAge() == -1 ? null : Instant.now().plusSeconds(cookie.getMaxAge())
                )
        );
    }
}
