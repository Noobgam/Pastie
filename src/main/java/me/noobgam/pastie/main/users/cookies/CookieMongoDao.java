package me.noobgam.pastie.main.users.cookies;

import com.mongodb.client.model.Filters;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CookieMongoDao implements CookieDao {

    private final MongoAsyncCollectionX<ObjectId, Cookie> collection;

    public CookieMongoDao(MongoAsyncCollectionX<ObjectId, Cookie> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Optional<Cookie>> findByCookie(@Nonnull String cookie) {
        return collection.findOneByFilter(Filters.eq(""));
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
