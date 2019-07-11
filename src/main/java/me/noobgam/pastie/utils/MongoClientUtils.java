package me.noobgam.pastie.utils;

import com.mongodb.MongoCommandException;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoClient;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;

import java.util.function.Consumer;
import java.util.function.Function;

public final class MongoClientUtils {
    private MongoClientUtils() {

    }

    public static <R> R inTransaction(
            MongoClient mongoClient,
            Function<ClientSession, R> callable
    ) {
        final ClientSession session;
        {
            MongoAsyncCollectionX.SimpleFutureCallback<ClientSession>
                    callback = new MongoAsyncCollectionX.SimpleFutureCallback<>();
            mongoClient.startSession(callback);
            session = callback.getFuture().join();
        }
        try {
            session.startTransaction();
            R result = callable.apply(session);
            MongoAsyncCollectionX.SimpleFutureCallback<Void>
                    callback = new MongoAsyncCollectionX.SimpleFutureCallback<>();
            session.commitTransaction(callback);
            callback.getFuture().join();
            return result;
        } catch (MongoCommandException exception) {
            MongoAsyncCollectionX.SimpleFutureCallback<Void>
                    callback = new MongoAsyncCollectionX.SimpleFutureCallback<>();
            session.abortTransaction(callback);
            callback.getFuture().join();
            throw exception;
        } finally {
            session.close();
        }
    }

    public static void inTransaction(
            MongoClient mongoClient,
            Consumer<ClientSession> callable
    ) {
        final ClientSession session;
        {
            MongoAsyncCollectionX.SimpleFutureCallback<ClientSession>
                    callback = new MongoAsyncCollectionX.SimpleFutureCallback<>();
            mongoClient.startSession(callback);
            session = callback.getFuture().join();
        }
        try {
            session.startTransaction();
            callable.accept(session);
            MongoAsyncCollectionX.SimpleFutureCallback<Void>
                    callback = new MongoAsyncCollectionX.SimpleFutureCallback<>();
            session.commitTransaction(callback);
            callback.getFuture().join();
        } catch (MongoCommandException exception) {
            MongoAsyncCollectionX.SimpleFutureCallback<Void>
                    callback = new MongoAsyncCollectionX.SimpleFutureCallback<>();
            session.abortTransaction(callback);
            callback.getFuture().join();
            throw exception;
        } finally {
            session.close();
        }
    }
}
