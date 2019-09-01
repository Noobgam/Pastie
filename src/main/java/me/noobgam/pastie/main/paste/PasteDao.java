package me.noobgam.pastie.main.paste;

import javax.annotation.CheckReturnValue;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PasteDao {
    @CheckReturnValue
    CompletableFuture<List<Paste>> findAll();

    @CheckReturnValue
    CompletableFuture<Optional<Paste>> findById(String id);

    @CheckReturnValue
    CompletableFuture<List<Paste>> findByHandle(String handle);

    @CheckReturnValue
    CompletableFuture<Void> insertOne(Paste paste);
}
