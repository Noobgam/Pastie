package me.noobgam.pastie.main.users;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class User {
    @BsonId
    private ObjectId id;

    private String username;

    public User(String username) {
        this(ObjectId.get(), username);
    }

    @BsonCreator
    public User(
            @BsonId ObjectId id,
            String username
    ) {
        this.id = id;
        this.username = username;
    }

    public ObjectId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
