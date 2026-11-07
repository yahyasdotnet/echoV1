package ma.yahyas.musicrec.theory;

import java.time.Instant;
import ma.yahyas.musicrec.theory.enums.SourceService;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class UserMetadata {

    private String username;

    @Enumerated(EnumType.STRING)
    private SourceService sourceService;

    private Integer dataWindowDays;
    private Long totalScrobbles;
    private Instant generatedAt;
}