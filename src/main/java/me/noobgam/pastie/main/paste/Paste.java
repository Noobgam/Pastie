package me.noobgam.pastie.main.paste;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Paste {
    @BsonId
    private String id;

    @BsonProperty
    private String content;

    public Paste(
            @BsonId String id,
            String content
    ) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
