package ma.yahyas.musicrec.theory.repository;

import java.util.List;
import java.util.Optional;

import ma.yahyas.musicrec.theory.TasteProfile;
import ma.yahyas.musicrec.theory.enums.SourceService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasteProfileRepository extends JpaRepository<TasteProfile, Long> {

    Optional<TasteProfile> findByUserId(Long userId);

    List<TasteProfile> findByMetadataUsername(String username);

    List<TasteProfile> findByMetadataSourceService(SourceService sourceService);

    List<TasteProfile> findByMetadataUsernameAndMetadataSourceService(String username, SourceService sourceService);
}
