package ma.yahyas.musicrec.theory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class ScrobblesDataService {

    private static final String LAST_FM_API_URL = "https://ws.audioscrobbler.com/2.0/";
    private static final List<String> SUPPORTED_PERIODS =
            List.of("7day", "1month", "3month", "6month", "12month", "overall");

    private final RestClient restClient;
    private final String apiKey;

    public ScrobblesDataService(
            @Value("${musicrec.lastfm.api-key:}") String apiKey) {
        this.restClient = RestClient.create();
        this.apiKey = apiKey;
    }

    public UserSummary fetchUserSummary(String username, String period, int limit) {
        requireText(username, "username");
        if (!SUPPORTED_PERIODS.contains(period)) {
            throw new IllegalArgumentException("Unsupported Last.fm period: " + period);
        }
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }

        JsonNode artistsResponse = request("user.getTopArtists", username, period, limit);
        JsonNode albumsResponse = request("user.getTopAlbums", username, period, limit);
        JsonNode tracksResponse = request("user.getTopTracks", username, period, limit);
        JsonNode userResponse = request("user.getInfo", username, null, null);

        return new UserSummary(
                artists(artistsResponse.path("topartists").path("artist")),
                albums(albumsResponse.path("topalbums").path("album")),
                tracks(tracksResponse.path("toptracks").path("track")),
                userResponse.path("user").path("playcount").asLong(0));
    }

    private JsonNode request(String method, String username, String period, Integer limit) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Set LASTFM_API_KEY before requesting Last.fm data");
        }

        JsonNode response = restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .scheme("https")
                            .host("ws.audioscrobbler.com")
                            .path("/2.0/")
                            .queryParam("method", method)
                            .queryParam("user", username)
                            .queryParam("api_key", apiKey)
                            .queryParam("format", "json");
                    if (period != null) {
                        builder.queryParam("period", period);
                    }
                    if (limit != null) {
                        builder.queryParam("limit", limit);
                    }
                    return builder.build();
                })
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("Last.fm returned an empty response for " + method);
        }
        if (response.has("error")) {
            throw new IllegalStateException("Last.fm error " + response.path("error").asText()
                    + ": " + response.path("message").asText());
        }
        return response;
    }

    private static List<Artist> artists(JsonNode node) {
        return elements(node).stream()
                .map(item -> new Artist(
                        item.path("name").asText(),
                        item.path("playcount").asLong(0),
                        item.path("mbid").asText()))
                .toList();
    }

    private static List<Album> albums(JsonNode node) {
        return elements(node).stream()
                .map(item -> new Album(
                        item.path("name").asText(),
                        item.path("artist").path("name").asText(),
                        item.path("playcount").asLong(0)))
                .toList();
    }

    private static List<Track> tracks(JsonNode node) {
        return elements(node).stream()
                .map(item -> new Track(
                        item.path("name").asText(),
                        item.path("artist").path("name").asText(),
                        item.path("playcount").asLong(0)))
                .toList();
    }

    private static List<JsonNode> elements(JsonNode node) {
        if (node.isArray()) {
            List<JsonNode> elements = new ArrayList<>();
            node.forEach(elements::add);
            return elements;
        }
        return node.isObject() ? List.of(node) : List.of();
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    public record UserSummary(
            List<Artist> topArtists,
            List<Album> topAlbums,
            List<Track> topTracks,
            long totalScrobbles) {
        public UserSummary {
            topArtists = List.copyOf(Objects.requireNonNull(topArtists));
            topAlbums = List.copyOf(Objects.requireNonNull(topAlbums));
            topTracks = List.copyOf(Objects.requireNonNull(topTracks));
        }
    }

    public record Artist(String name, long playCount, String musicBrainzId) {}

    public record Album(String name, String artist, long playCount) {}

    public record Track(String name, String artist, long playCount) {}
}
