package me.noobgam.pastie.core.mongo;

import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadPreference;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import me.noobgam.pastie.core.mongo.codec.ConventionAnnotationWrappingImpl;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author noobgam
 */
public class MongoAsyncCollectionX<TId, TEntity> {

    public static final Bson EMPTY_FILTER = new BsonDocument();

    private static final UpdateOptions UPSERT = new UpdateOptions().upsert(true);
    private static final List<Convention> DEFAULT_CONVENTIONS =
            Collections.singletonList(new ConventionAnnotationWrappingImpl());

    private final MongoCollection<TEntity> collection;
    private final CodecRegistry codecRegistry;
    private final MongoIdHacker<TId, TEntity> idHacker;

    public MongoAsyncCollectionX(
            MongoCollection<TEntity> mongoCollection,
            Class<TEntity> entityClass,
            Class<?>... extraClasses
    ) {
        codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                        PojoCodecProvider.builder()
                                .conventions(DEFAULT_CONVENTIONS)
                                .register(entityClass)
                                .register(extraClasses).build()
                )
        );
        this.collection = mongoCollection.withCodecRegistry(codecRegistry);
        this.idHacker = MongoIdHacker.cons(entityClass);
    }

    public MongoAsyncCollectionX(
            MongoCollection<TEntity> mongoCollection,
            ClassModel<TEntity> entityClassModel,
            ClassModel<?>... extraClassesModels
    ) {
        codecRegistry =
                CodecRegistries.fromProviders(
                        PojoCodecProvider.builder()
                                .conventions(DEFAULT_CONVENTIONS)
                                .register(entityClassModel)
                                .register(extraClassesModels).automatic(true).build()
                );
        this.collection = mongoCollection.withCodecRegistry(codecRegistry);
        this.idHacker = MongoIdHacker.cons(entityClassModel.getType());
    }

    public MongoNamespace getNamespace() {
        return collection.getNamespace();
    }

    /**
     * drop collection
     *
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Void> drop() {
        SimpleFutureCallback<Void> callback = new SimpleFutureCallback<>();
        collection.drop(callback);
        return callback.getFuture();
    }

    /**
     * Insert one document transformed from entity
     *
     * @param entity document
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Void> insertOne(TEntity entity) {
        SimpleFutureCallback<Void> callback = new SimpleFutureCallback<>();
        collection.insertOne(entity, callback);
        return callback.getFuture();
    }

    /**
     * Insert one document transformed from entity
     *
     * @param filter filter
     * @return {@link CompletableFuture<DeleteResult>} future
     */
    @CheckReturnValue
    public CompletableFuture<DeleteResult> deleteMany(Bson filter) {
        SimpleFutureCallback<DeleteResult> callback = new SimpleFutureCallback<>();
        collection.deleteMany(filter, callback);
        return callback.getFuture();
    }

    /**
     * Insert one document transformed from entity
     *
     * @param entity document
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Void> insertOne(
            TEntity entity,
            ClientSession clientSession
    ) {
        SimpleFutureCallback<Void> callback = new SimpleFutureCallback<>();
        collection.insertOne(clientSession, entity, callback);
        return callback.getFuture();
    }

    /**
     * Insert one document transformed from entity
     *
     * @param filter document
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Long> count(
            Bson filter,
            CountOptions countOptions
    ) {
        SimpleFutureCallback<Long> callback = new SimpleFutureCallback<>();
        collection.countDocuments(filter, countOptions, callback);
        return callback.getFuture();
    }

    /**
     * Insert many documents transformed from entity
     *
     * @param entities documents
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Void> insertMany(List<TEntity> entities) {
        SimpleFutureCallback<Void> callback = new SimpleFutureCallback<>();
        collection.insertMany(entities, callback);
        return callback.getFuture();
    }

    /**
     * Find one by filter
     *
     * @param filter filter
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Optional<TEntity>> findOneByFilter(Bson filter) {
        FindOneParseFutureCallback<TEntity> callback = new FindOneParseFutureCallback<>();
        prepareIterable(filter, null, 0, 1, null, null).first(callback);
        return callback.getFuture();
    }

    /**
     * Find by id
     *
     * @param id document
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<Optional<TEntity>> findById(TId id) {
        FindOneParseFutureCallback<TEntity> callback = new FindOneParseFutureCallback<>();
        prepareIterable(Filters.eq("_id", id), null, 0, 1, null, null).first(callback);
        return callback.getFuture();
    }

    /**
     * Find by id
     *
     * @param id         document
     * @param projection projection
     * @param clazz      resulting class
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public <TProjection> CompletableFuture<Optional<TProjection>> findById(TId id, Bson projection, Class<TProjection> clazz) {
        FindOneParseFutureCallback<TProjection> callback = new FindOneParseFutureCallback<>();
        prepareProjectIterable(Filters.eq("_id", id), null, -1, -1, null, null, projection, clazz).first(callback);
        return callback.getFuture();
    }

    /**
     * Find by bson
     *
     * @param filter document
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<List<TEntity>> find(Bson filter) {
        CompletableFuture<List<TEntity>> result = new CompletableFuture<>();
        List<TEntity> items = new ArrayList<>();
        prepareIterable(filter, null, -1, -1, null, null)
                .forEach(items::add, (dummy, ex) -> {
                    if (ex != null) {
                        result.completeExceptionally(ex);
                    } else {
                        result.complete(items);
                    }
                });
        return result;
    }

    /**
     * Find by bson
     *
     * @param filter     document
     * @param projection projection
     * @param clazz      resulting class
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public <TProjection> CompletableFuture<List<TProjection>> find(Bson filter, Bson projection, Class<TProjection> clazz) {
        CompletableFuture<List<TProjection>> result = new CompletableFuture<>();
        List<TProjection> items = new ArrayList<>();
        prepareProjectIterable(filter, null, -1, -1, null, null, projection, clazz)
                .forEach(items::add, (dummy, ex) -> {
                    if (ex != null) {
                        result.completeExceptionally(ex);
                    } else {
                        result.complete(items);
                    }
                });
        return result;
    }

    /**
     * Find by bson
     *
     * @param filter     document
     * @param projection projection
     * @param clazz      resulting class
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public <TProjection> CompletableFuture<Void> forEach(
            Bson filter,
            Bson projection,
            Class<TProjection> clazz,
            Block<? super TProjection> consumer
    ) {
        SimpleFutureCallback<Void> result = new SimpleFutureCallback<>();
        prepareProjectIterable(filter, null, -1, -1, null, null, projection, clazz)
                .forEach(consumer, (dummy, ex) -> {
                    if (ex != null) {
                        result.getFuture().completeExceptionally(ex);
                    } else {
                        result.getFuture().complete(null);
                    }
                });
        return result.getFuture();
    }

    /**
     * Replace entity by id
     *
     * @param entity
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<UpdateResult> replaceById(TEntity entity) {
        SimpleFutureCallback<UpdateResult> callback = new SimpleFutureCallback<>();
        collection.replaceOne(Filters.eq("_id", idHacker.getId(entity)), entity, callback);
        return callback.getFuture();
    }

    /**
     * Replace entity by id
     *
     * @param entity
     * @return {@link CompletableFuture<Void>} future
     */
    @CheckReturnValue
    public CompletableFuture<UpdateResult> upsert(TEntity entity) {
        SimpleFutureCallback<UpdateResult> callback = new SimpleFutureCallback<>();
        collection.replaceOne(Filters.eq("_id", idHacker.getId(entity)), entity, UPSERT, callback);
        return callback.getFuture();
    }

    @CheckReturnValue
    public CompletableFuture<List<TEntity>> findAll() {
        CompletableFuture<List<TEntity>> result = new CompletableFuture<>();
        List<TEntity> items = new ArrayList<>();
        prepareIterable(null, null, -1, -1, null, null)
                .forEach(items::add, (dummy, ex) -> {
                    if (ex != null) {
                        result.completeExceptionally(ex);
                    } else {
                        result.complete(items);
                    }
                });
        return result;
    }

    @CheckReturnValue
    public CompletableFuture<List<BsonDocument>> aggregate(Bson... pipeline) {
        CompletableFuture<List<BsonDocument>> result = new CompletableFuture<>();
        List<BsonDocument> items = new ArrayList<>();
        prepareAggregate(null, pipeline)
                .forEach(items::add, (dummy, ex) -> {
                    if (ex != null) {
                        result.completeExceptionally(ex);
                    } else {
                        result.complete(items);
                    }
                });
        return result;
    }

    protected AggregateIterable<BsonDocument> prepareAggregate(
            @Nullable ReadPreference readPreference,
            Bson... pipeline
    ) {
        MongoCollection<BsonDocument> c =
                getCollection(readPreference).withDocumentClass(BsonDocument.class);
        return c.aggregate(Arrays.asList(pipeline));
    }

    protected FindIterable<TEntity> prepareIterable(
            @Nullable Bson filter,
            @Nullable Bson order,
            int skip,
            int limit,
            @Nullable ReadPreference readPreference,
            @Nullable Duration timeout
    ) {
        MongoCollection<TEntity> c = getCollection(readPreference);
        FindIterable<TEntity> iterable = filter != null ? c.find(filter) : c.find();
        if (order != null) {
            iterable.sort(order);
        }
        if (limit != -1) {
            iterable.limit(limit);
        }
        if (skip != -1) {
            iterable.skip(skip);
        }
        if (timeout != null) {
            iterable.maxTime(timeout.getNano(), TimeUnit.NANOSECONDS);
        }
        return iterable;
    }

    protected <TProjection> FindIterable<TProjection> prepareProjectIterable(
            @Nullable Bson filter,
            @Nullable Bson order,
            int skip,
            int limit,
            @Nullable ReadPreference readPreference,
            @Nullable Duration timeout,
            Bson projection,
            Class<TProjection> clazz
    ) {
        MongoCollection<TProjection> c = getCollection(readPreference).withDocumentClass(clazz);
        FindIterable<TProjection> iterable = filter != null ? c.find(filter) : c.find();
        iterable.projection(projection);
        if (order != null) {
            iterable.sort(order);
        }
        if (limit != -1) {
            iterable.limit(limit);
        }
        if (skip != -1) {
            iterable.skip(skip);
        }
        if (timeout != null) {
            iterable.maxTime(timeout.getNano(), TimeUnit.NANOSECONDS);
        }
        return iterable;
    }

    private MongoCollection<TEntity> getCollection(@Nullable ReadPreference readPreference) {
        return readPreference == null ? collection : collection.withReadPreference(readPreference);
    }

    /**
     * Simple callback suitable for most operations except 'find' operations due to specific behavior
     *
     * @param <T> callback result type
     */
    public static class SimpleFutureCallback<T> implements SingleResultCallback<T> {

        private final CompletableFuture<T> future = new CompletableFuture<>();

        @CheckReturnValue
        public CompletableFuture<T> getFuture() {
            return future;
        }

        @Override
        public void onResult(T result, Throwable throwable) {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                future.complete(result);
            }
        }
    }

    /**
     * Callback for 'findOne' operations with pojo response
     *
     * @param <T> callback result type
     */
    private static class FindOneParseFutureCallback<T> implements SingleResultCallback<T> {

        private final CompletableFuture<Optional<T>> future = new CompletableFuture<>();

        @CheckReturnValue
        public CompletableFuture<Optional<T>> getFuture() {
            return future;
        }

        @Override
        public void onResult(T result, Throwable throwable) {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else if (result != null) {
                try {
                    future.complete(Optional.of(result));
                } catch (RuntimeException parseEx) {
                    future.completeExceptionally(parseEx);
                }
            } else {
                future.complete(Optional.empty());
            }
        }
    }
}

