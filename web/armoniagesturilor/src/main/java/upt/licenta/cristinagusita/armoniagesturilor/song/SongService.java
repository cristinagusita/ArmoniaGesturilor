package upt.licenta.cristinagusita.armoniagesturilor.song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import upt.licenta.cristinagusita.armoniagesturilor.achievements.AchievementService;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    public SongService(SongRepository songRepository, AppUserRepository appUserRepository) {
        this.songRepository = songRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Song save(Song song) {
        return songRepository.save(song);
    }

    @Transactional(readOnly = true)
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Song> findByUserId(Long userId) {
        return songRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Song> findAll() {
        List<Song> songs = songRepository.findByIsPublicTrue();
        // sort songs descending by id
        songs.sort((s1, s2) -> (int) (s2.getId() - s1.getId()));
        return songs;
    }


    @Transactional
    public ResponseEntity<?> deleteSong(Long id, Principal principal) {
        Optional<Song> songOpt = songRepository.findById(id);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            String username = principal.getName();
            if (song.getUser().getEmail().equals(username)) {
                // go through the user whose song was deleted and remove achievements if conditions are no longer met
                achievementService.updateAchievementStatus(song.getUser());
                songRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to delete this song");
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    public ResponseEntity<?> toggleSongVisibility(@PathVariable Long id) {
        return songRepository.findById(id).map(song -> {
            song.togglePublicPrivate();
            songRepository.save(song);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<?> likeSong(Long id, Principal principal) {
        return songRepository.findById(id).map(song -> {
            String username = principal.getName();
            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);

            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

            if (song.getLikedByUsers().contains(user)) {
                return ResponseEntity.badRequest().body("User has already liked this song");
            } else {
                song.getLikedByUsers().add(user);
                songRepository.save(song);

                return ResponseEntity.ok(song.getLikedByUsers().size());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<?> dislikeSong(Long id, Principal principal) {
        return songRepository.findById(id).map(song -> {
            String username = principal.getName();
            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);

            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

            if (!song.getLikedByUsers().contains(user)) {
                return ResponseEntity.badRequest().body("User has not liked this song");
            } else {
                song.getLikedByUsers().remove(user);
                songRepository.save(song);

                return ResponseEntity.ok(song.getLikedByUsers().size());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> checkIfLiked(Long id, Principal principal) {
        return songRepository.findById(id).map(song -> {
            String username = principal.getName();
            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);

            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

            if (song.getLikedByUsers().contains(user)) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    public List<Song> findAllSongs() {
        return songRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> deleteSongAdmin(Long songId) {
        try {
            Optional<Song> song = songRepository.findById(songId);
            if (!song.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Song not found");
            }
            // go through the user whose song was deleted and remove achievements if conditions are no longer met
            achievementService.updateAchievementStatus(song.get().getUser());
            songRepository.deleteById(songId);
            return ResponseEntity.ok("Song deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting song: " + e.getMessage());
        }
    }


    @Transactional
    public void deleteLikesByUserId(Long userId) {
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            song.getLikedByUsers().removeIf(user -> user.getId().equals(userId));
        }
    }
}