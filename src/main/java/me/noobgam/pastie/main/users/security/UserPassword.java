package me.noobgam.pastie.main.users.security;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class UserPassword {
    @BsonId
    private final ObjectId userId;

    private final byte[] password;

    public UserPassword(
            @BsonId ObjectId userId,
            byte[] password
    ) {
        this.userId = userId;
        this.password = password;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public byte[] getPassword() {
        return password;
    }
}
