package ma.yahyas.musicrec.theory.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import ma.yahyas.musicrec.theory.TasteProfile;
import ma.yahyas.musicrec.theory.UserMetadata;
import ma.yahyas.musicrec.theory.enums.SourceService;
import ma.yahyas.musicrec.theory.repository.TasteProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class TasteReportService {

    private static final int ARTIST_LIMIT = 10;

    private final TasteProfileRepository tasteProfileRepository;
    private final ScrobblesDataService scrobblesDataService;
    private final MusicBrainzDataService musicBrainzDataService;

    public TasteReportService(
            TasteProfileRepository tasteProfileRepository,
            ScrobblesDataService scrobblesDataService,
            MusicBrainzDataService musicBrainzDataService) {
        this.tasteProfileRepository = tasteProfileRepository;
        this.scrobblesDataService = scrobblesDataService;
        this.musicBrainzDataService = musicBrainzDataService;
    }

    public TasteProfile generateAndSave(Long userId, String username, String period) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }

        ScrobblesDataService.UserSummary summary =
                scrobblesDataService.fetchUserSummary(username, period, ARTIST_LIMIT);

        Map<String, Integer> genreArtistCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<String, String> genreNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        summary.topArtists().stream()
                .filter(artist -> artist.musicBrainzId() != null && !artist.musicBrainzId().isBlank())
                .forEach(artist -> musicBrainzDataService.fetchArtist(artist.musicBrainzId()).tags()
                        .stream()
                        .filter(tag -> !tag.isBlank())
                        .forEach(tag -> {
                            genreNames.putIfAbsent(tag.toLowerCase(Locale.ROOT), tag);
                            genreArtistCounts.merge(tag, 1, Integer::sum);
                        }));

        List<String> favoriteGenres = genreArtistCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER))
                .map(entry -> genreNames.get(entry.getKey().toLowerCase(Locale.ROOT)))
                .toList();
        List<String> genreBridges = genreArtistCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> genreNames.get(entry.getKey().toLowerCase(Locale.ROOT)))
                .toList();

        TasteProfile profile = new TasteProfile();
        profile.setUserId(userId);
        profile.setMetadata(metadata(username, period, summary.totalScrobbles()));
        profile.setFavoriteGenres(favoriteGenres);
        profile.setFavoriteArtists(summary.topArtists().stream()
                .map(ScrobblesDataService.Artist::name)
                .toList());
        profile.setTopAlbums(summary.topAlbums().stream()
                .map(album -> album.artist() + " - " + album.name())
                .toList());
        profile.setTopTracks(summary.topTracks().stream()
                .map(track -> track.artist() + " - " + track.name())
                .toList());
        profile.setUniqueArtists(summary.topArtists().size());
        profile.setUniqueGenres(favoriteGenres.size());
        profile.setGenreBridges(genreBridges);

        return tasteProfileRepository.save(profile);
    }

    private static UserMetadata metadata(String username, String period, long totalScrobbles) {
        UserMetadata metadata = new UserMetadata();
        metadata.setUsername(username);
        metadata.setSourceService(SourceService.LASTFM);
        metadata.setDataWindowDays(daysIn(period));
        metadata.setTotalScrobbles(totalScrobbles);
        metadata.setGeneratedAt(Instant.now());
        return metadata;
    }

    private static Integer daysIn(String period) {
        return switch (period) {
            case "7day" -> 7;
            case "1month" -> 30;
            case "3month" -> 90;
            case "6month" -> 180;
            case "12month" -> 365;
            case "overall" -> null;
            default -> throw new IllegalArgumentException("Unsupported Last.fm period: " + period);
        };
    }
}
