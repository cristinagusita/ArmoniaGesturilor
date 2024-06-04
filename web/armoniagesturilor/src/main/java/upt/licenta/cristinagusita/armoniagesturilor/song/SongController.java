package upt.licenta.cristinagusita.armoniagesturilor.song;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class SongController {

    @Autowired
    private SongService songService;
    private AppUserService userService;

    @PostMapping("/uploadSong")
    public ResponseEntity<?> uploadSong(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            AppUser user = (AppUser) userService.loadUserByUsername(principal.getName());

            Song song = new Song();

            song.setUser(user);
            song.setFileName(file.getOriginalFilename());
            song.setContentType(file.getContentType());
            song.setData(file.getBytes());
            song.setDateCreated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            song.setIsPublic(false);
            //songService.addSong(song, user);

            songService.save(song);

            return ResponseEntity.ok().body("Song uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload song");
        }
    }

    @GetMapping("/songs/data/{songId}")
    @Transactional
    public ResponseEntity<ByteArrayResource> getSongData(@PathVariable Long songId, Principal principal) {
        Optional<Song> songOpt = songService.findById(songId);

        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            byte[] data = song.getData();
            HttpHeaders headers = new HttpHeaders();

            ByteArrayResource resource = new ByteArrayResource(data);

            headers.setContentType(MediaType.parseMediaType(song.getContentType()));


            return new ResponseEntity<ByteArrayResource>(resource, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/songs/data/discover/{songId}")
    public ResponseEntity<byte[]> getSongDataDiscover(@PathVariable Long songId, Principal principal) {
        Optional<Song> songOpt = songService.findById(songId);

        if (songOpt.isPresent()) {
            Song song = songOpt.get();

            if (!song.getIsPublic()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            byte[] data = song.getData();
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.parseMediaType(song.getContentType()));

            headers.setContentDispositionFormData("filename", song.getFileName());

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("songs/toggle/{id}")
    public ResponseEntity<?> toggleSongVisibility(@PathVariable Long id) {
        return songService.toggleSongVisibility(id);
    }

    @PostMapping("songs/like/{id}")
    public ResponseEntity<?> likeSong(@PathVariable Long id, Principal principal) {
        return songService.likeSong(id, principal);
    }

    @PostMapping("songs/dislike/{id}")
    public ResponseEntity<?> dislikeSong(@PathVariable Long id, Principal principal) {
        return songService.dislikeSong(id, principal);
    }

    @PostMapping("songs/checkLike/{id}")
    public ResponseEntity<?> checkLike(@PathVariable Long id, Principal principal) {
        return songService.checkIfLiked(id, principal);
    }

    @DeleteMapping("/songs/delete/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id, Principal principal) {
        return songService.deleteSong(id, principal);
    }

}

