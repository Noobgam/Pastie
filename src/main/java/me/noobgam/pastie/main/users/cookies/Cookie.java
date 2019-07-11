package me.noobgam.pastie.main.users.cookies;

import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.utils.RandomUtils;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public class Cookie {
    public static Cookie DUMMY_COOKIE
            = new Cookie(
            "DUMMY",
            new ObjectId(PropertiesHolder.getProperty("dummy.user.id")),
            null
    );

    @BsonId
    private String cookie;

    private ObjectId userId;

    // null for dummy.
    @Nullable
    private Instant expireDate;

    public Cookie(ObjectId userId, @Nonnull Instant expireDate) {
        this(RandomUtils.generateSecureString(), userId, expireDate);
    }

    @BsonCreator
    public Cookie(
            @BsonId String cookie,
            ObjectId userId,
            @Nullable Instant expireDate
    ) {
        this.cookie = cookie;
        this.userId = userId;
        this.expireDate = expireDate;
    }

    public String getCookie() {
        return cookie;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public Instant getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Instant expireDate) {
        this.expireDate = expireDate;
    }
}
