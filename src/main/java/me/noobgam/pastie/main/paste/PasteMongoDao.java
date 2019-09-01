package me.noobgam.pastie.main.paste;

import com.mongodb.client.model.Filters;
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

    @Override
    public CompletableFuture<List<Paste>> findByHandle(String handle) {
        return collection.find(Filters.eq("owner", handle));
    }

    @Override
    public CompletableFuture<Void> insertOne(Paste paste) {
        return collection.insertOne(paste);
    }
}
