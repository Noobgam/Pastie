package me.noobgam.pastie.main.users.user;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface UserDao {

    CompletableFuture<Optional<User>> findById(ObjectId id);

    CompletableFuture<Optional<User>> findByUsername(String name);

    CompletableFuture<List<User>> findAll();

    CompletableFuture<Set<String>> findAllHandles();
}
