package me.noobgam.pastie.main.users.security;

import org.bson.types.ObjectId;

import java.util.concurrent.CompletableFuture;

public interface UserPasswordDao {
    CompletableFuture<Boolean> validateCredentialPair(
            ObjectId id,
            byte[] password
    );
}
