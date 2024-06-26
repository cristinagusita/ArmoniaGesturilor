package upt.licenta.cristinagusita.armoniagesturilor.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByUserId(Long userId);

    List<Song> findByIsPublicTrue();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteById(Long id);
}