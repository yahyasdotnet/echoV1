package ma.yahyas.musicrec.theory;

import java.util.List;
import java.util.Map;

import ma.yahyas.musicrec.theory.enums.Decade;
import ma.yahyas.musicrec.theory.enums.TasteLabel;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


/**
 * A user's taste profile, generated from their listening history.
 * Scores are doubles between 0.0 and 1.0.
 */
@Getter
@Setter
@Entity
public class TasteProfile {

    @Id
    private Long userId;

    @Embedded
    private UserMetadata metadata;

    // Core identity
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> favoriteGenres;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> favoriteArtists;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> topAlbums;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> topTracks;

    // Range of preferences
    private Double genreDiversity;
    private Double artistConcentration;
    private Integer uniqueArtists;
    private Integer uniqueGenres;
    @Enumerated(EnumType.STRING)
    private TasteLabel tasteLabel;

    // Era (decade -> share of listening, 0.0 to 1.0)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Decade, Double> eraPreferences;

    // Mainstream vs. niche
    private Double obscurityScore;
    private Double hipsternessScore;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> mainstreamArtists;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> deepCutArtists;

    // Behavior and rhythm
    private Double discoveryRate;                 // new artists per month
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Integer> listeningPatterns; // e.g. "FRIDAY" -> plays
    private Integer averageListeningSessionLength;  // minutes
    private Double albumVsShufflePreference;        // 1.0 = always full albums

    // Bridges and crossovers
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> crossoverArtists;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> genreBridges;
}