package me.noobgam.pastie.main.paste;

import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PasteMongoDao implements PasteDao {

    private final MongoAsyncCollectionX<String, Paste> collection;

    public PasteMongoDao(MongoAsyncCollectionX<String, Paste> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<List<Paste>> findAll() {
        return collection.findAll();
    }

    @Override
    public CompletableFuture<Optional<Paste>> findById(String id) {
        return collection.findById(id);
    }
}
