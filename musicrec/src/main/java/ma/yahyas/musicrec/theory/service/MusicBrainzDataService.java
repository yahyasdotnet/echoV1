package ma.yahyas.musicrec.theory.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class MusicBrainzDataService {

    private static final long REQUEST_INTERVAL_MILLIS = 1_000;
    private static long nextRequestAtMillis;

    private final RestClient restClient;
    private final String userAgent;

    public MusicBrainzDataService(
            @Value("${musicrec.musicbrainz.user-agent:}") String userAgent) {
        this.restClient = RestClient.builder()
                .baseUrl("https://musicbrainz.org/ws/2")
                .build();
        this.userAgent = userAgent;
    }

    public ArtistMetadata fetchArtist(String musicBrainzId) {
        if (musicBrainzId == null || musicBrainzId.isBlank()) {
            throw new IllegalArgumentException("musicBrainzId must not be blank");
        }
        if (userAgent == null || userAgent.isBlank()) {
            throw new IllegalStateException(
                    "Set MUSICBRAINZ_USER_AGENT before requesting MusicBrainz data");
        }
        waitForRateLimit();

        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/artist/{id}")
                        .queryParam("inc", "tags")
                        .queryParam("fmt", "json")
                        .build(musicBrainzId))
                .header("User-Agent", userAgent)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("MusicBrainz returned an empty artist response");
        }

        return new ArtistMetadata(
                response.path("id").asText(),
                response.path("name").asText(),
                response.path("country").asText(),
                response.path("begin-area").path("name").asText(),
                response.path("life-span").path("begin").asText(),
                response.path("disambiguation").asText(),
                response.path("tags").findValuesAsText("name"));
    }

    private static void waitForRateLimit() {
        synchronized (MusicBrainzDataService.class) {
            long waitMillis = nextRequestAtMillis - System.currentTimeMillis();
            if (waitMillis > 0) {
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted waiting to call MusicBrainz", exception);
                }
            }
            nextRequestAtMillis = System.currentTimeMillis() + REQUEST_INTERVAL_MILLIS;
        }
    }

    public record ArtistMetadata(
            String musicBrainzId,
            String name,
            String country,
            String beginArea,
            String beginDate,
            String disambiguation,
            List<String> tags) {
        public ArtistMetadata {
            tags = List.copyOf(Objects.requireNonNull(tags));
        }
    }
}
