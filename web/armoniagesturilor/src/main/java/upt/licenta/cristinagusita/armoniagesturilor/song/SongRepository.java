package upt.licenta.cristinagusita.armoniagesturilor.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    // Find songs by user ID
    List<Song> findByUserId(Long userId);

    List<Song> findByIsPublicTrue();
}