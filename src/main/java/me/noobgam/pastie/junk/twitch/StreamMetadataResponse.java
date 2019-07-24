package me.noobgam.pastie.junk.twitch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamMetadataResponse {
    @JsonProperty("data")
    private List<StreamMetadataPojo> data;

    @JsonCreator
    public StreamMetadataResponse(
            @JsonProperty("data") List<StreamMetadataPojo> data
    ) {
        this.data = data;
    }

    public List<StreamMetadataPojo> getData() {
        return data;
    }
}
