package me.noobgam.pastie.main.paste;

import org.bson.codecs.pojo.annotations.BsonId;

public class Paste {
    @BsonId
    private String id;

    private String owner;

    private String content;

    public Paste(
            @BsonId String id,
            String owner,
            String content
    ) {
        this.id = id;
        this.owner = owner;
        this.content = content;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
