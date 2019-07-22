package me.noobgam.pastie.main.users.cookies;

import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CookieDao {

    CompletableFuture<Optional<Cookie>> findByCookie(@Nonnull String cookie);

    CompletableFuture<List<Cookie>> findAll();

    CompletableFuture<DeleteResult> deAuthUser(ObjectId userId);

    CompletableFuture<Void> storeCookie(ObjectId userId, javax.servlet.http.Cookie cookie);
}
