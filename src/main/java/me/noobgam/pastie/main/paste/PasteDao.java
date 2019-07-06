package me.noobgam.pastie.main.paste;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PasteDao {
    CompletableFuture<List<Paste>> findAll();

    CompletableFuture<Optional<Paste>> findById(String id);
}
