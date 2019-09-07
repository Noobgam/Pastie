package me.noobgam.pastie.main.paste;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;

import javax.annotation.Nullable;
import java.time.Instant;

public class Paste {

    public static final Paste DUMMY =
            new Paste(null, null, null, null, null);

    @BsonId
    private String id;

    private String owner;

    private String content;

    @Nullable
    private String language;

    @Nullable
    private Instant instant;

    @BsonCreator
    private Paste(
            @BsonId String id,
            String owner,
            String content,
            @Nullable String language,
            @Nullable Instant instant
    ) {
        this.id = id;
        this.owner = owner;
        this.content = content;
        this.language = language;
        this.instant = instant;
    }

    public static Paste cons(
            String id,
            String owner,
            String content,
            @Nullable String language
    ) {
        return new Paste(
                id,
                owner,
                content,
                language,
                Instant.now()
        );
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getOwner() {
        return owner;
    }

    public String getLanguage() {
        return language;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
