package me.noobgam.pastie.junk.twitch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.Gauge;
import me.noobgam.pastie.core.properties.PropertiesHolder;
import me.noobgam.pastie.main.background.Job;
import me.noobgam.pastie.utils.Cu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TwitchViewCountPullJob extends Job {

    private static final Logger logger = LogManager.getLogger(TwitchViewCountPullJob.class);

    private static final Set<String> watchedStreams = Cu.set(
            "redbull",
            "jsooonix",
            "riotgames",
            "loltyler1"
    );

    private static final URI uri =
            URI.create("https://api.twitch.tv/helix/streams" +
                    (watchedStreams.isEmpty() ? "" : "?user_login=" + String.join("&user_login=", watchedStreams)));

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private volatile Duration delay = Duration.ofSeconds(10);

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Gauge VIEWERS_COUNT_SUMMARY = Gauge.build()
            .name("viewers_count")
            .help("Active viewer count.")
            .labelNames("user_login").register();

    @Override
    protected Duration delay() {
        return delay;
    }

    @Override
    protected void run() {
        try {

            HttpResponse<String> httpResponse = httpClient.send(prepareRequest(), HttpResponse.BodyHandlers.ofString());
            StreamMetadataResponse response = MAPPER.readValue(httpResponse.body(), StreamMetadataResponse.class);
            Map<String, Integer> viewersByName
                    = response.getData().stream().collect(
                    Collectors.toMap(pojo -> pojo.getUserName(), pojo -> pojo.getViewerCount())
            );
            for (String streamer : watchedStreams) {
                VIEWERS_COUNT_SUMMARY
                        .labels(streamer)
                        .set(viewersByName.getOrDefault(streamer, 0));
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static HttpRequest prepareRequest() {
        return HttpRequest.newBuilder(uri)
                .header("Client-ID", PropertiesHolder.getProperty("twitch.client.id"))
                .build();
    }
}
