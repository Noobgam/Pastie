package me.noobgam.pastie.main.users.security;

import org.bson.types.ObjectId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserPasswordDao {
    CompletableFuture<Optional<UserPassword>> findByIdAndPassword(
            ObjectId id,
            byte[] password
    );
}
