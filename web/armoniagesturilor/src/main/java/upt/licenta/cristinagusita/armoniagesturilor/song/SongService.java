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

//    @Transactional
//    public Song addSong(Song song, Principal principal) {
//        String username = principal.getName();
//        AppUser user = appUserRepository.findAppUserByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        song.setUser(user);
//        Song savedSong = songRepository.save(song);
//        user.getSongs().add(savedSong);
//        appUserRepository.save(user);
//        achievementService.checkAndAssignAchievements(user);
//        return savedSong;
//    }

//    @Transactional
//    public Song addSong(Song song, AppUser user) {
//        song.setUser(user);  // Set the user to the song
//        Song savedSong = songRepository.save(song);
//        user.getSongs().add(savedSong);
//        appUserRepository.save(user);
//        achievementService.checkAndAssignAchievements(user);
//        return savedSong;
//    }
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

   // @Transactional
//    public void deleteSong(Long id) {
//        songRepository.deleteById(id);
//    }
//    public ResponseEntity<?> deleteSong(Long id, Principal principal) {
//        return songRepository.findById(id).map(song -> {
//            // Assuming the principal's name is the username/email which can uniquely identify a user
//            String username = principal.getName();
//            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);
//
//            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));
//
//            // Check if the song belongs to the user
//            if (!song.getUser().equals(user)) {
//                return ResponseEntity.badRequest().body("Song does not belong to user");
//            } else {
//                songRepository.deleteById(id);
//                return ResponseEntity.ok().build();
//            }
//        }).orElse(ResponseEntity.notFound().build());
//    }

    @Transactional
    public ResponseEntity<?> deleteSong(Long id, Principal principal) {
        Optional<Song> songOpt = songRepository.findById(id);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            String username = principal.getName();
            if (song.getUser().getEmail().equals(username)) {
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
            // Assuming the principal's name is the username/email which can uniquely identify a user
            String username = principal.getName();
            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);

            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the user has already liked the song
            if (song.getLikedByUsers().contains(user)) {
                // User has already liked the song, do not increase the like count
                return ResponseEntity.badRequest().body("User has already liked this song");
            } else {
                // Add the user to the set of users who have liked the song
                song.getLikedByUsers().add(user);
                songRepository.save(song);

                // Return the updated like count or any other relevant information
                return ResponseEntity.ok(song.getLikedByUsers().size());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<?> dislikeSong(Long id, Principal principal) {
        return songRepository.findById(id).map(song -> {
            // Assuming the principal's name is the username/email which can uniquely identify a user
            String username = principal.getName();
            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);

            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the user has already liked the song
            if (!song.getLikedByUsers().contains(user)) {
                // User has not liked the song, do not decrease the like count
                return ResponseEntity.badRequest().body("User has not liked this song");
            } else {
                // Remove the user from the set of users who have liked the song
                song.getLikedByUsers().remove(user);
                songRepository.save(song);

                // Return the updated like count or any other relevant information
                return ResponseEntity.ok(song.getLikedByUsers().size());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> checkIfLiked(Long id, Principal principal) {
        return songRepository.findById(id).map(song -> {
            // Assuming the principal's name is the username/email which can uniquely identify a user
            String username = principal.getName();
            Optional<AppUser> userOpt = appUserRepository.findAppUserByEmail(username);

            AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the user has already liked the song
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
    public void deleteSongAdmin(Long songId) {
        songRepository.deleteById(songId);
    }

    @Transactional
    public void deleteLikesByUserId(Long userId) {
        // delete all the likes to songs given by user with id
        // go through the likedByUsers set of each song and remove the user with id
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            song.getLikedByUsers().removeIf(user -> user.getId().equals(userId));
        }
    }
}