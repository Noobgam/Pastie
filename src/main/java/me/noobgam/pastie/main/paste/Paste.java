package me.noobgam.pastie.main.paste;

import org.bson.codecs.pojo.annotations.BsonId;

import javax.annotation.Nullable;

public class Paste {

    public static final Paste DUMMY =
            new Paste(null, null, null, null);

    @BsonId
    private String id;

    private String owner;

    private String content;

    @Nullable
    private String language;

    public Paste(
            @BsonId String id,
            String owner,
            String content,
            @Nullable String language
    ) {
        this.id = id;
        this.owner = owner;
        this.content = content;
        this.language = language;
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
