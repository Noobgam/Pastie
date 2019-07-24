package me.noobgam.pastie.junk.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamMetadataPojo {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("viewer_count")
    private int viewerCount;

    public StreamMetadataPojo(
            @JsonProperty("user_id") String userId,
            @JsonProperty("user_name") String userName,
            @JsonProperty("viewer_count") int viewerCount
    ) {
        this.userId = userId;
        this.userName = userName;
        this.viewerCount = viewerCount;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getViewerCount() {
        return viewerCount;
    }
}
