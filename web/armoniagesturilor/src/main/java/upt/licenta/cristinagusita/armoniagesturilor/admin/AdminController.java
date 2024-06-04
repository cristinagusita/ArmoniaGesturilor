package upt.licenta.cristinagusita.armoniagesturilor.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.song.Song;
import upt.licenta.cristinagusita.armoniagesturilor.song.SongService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AppUserService userService;

    @Autowired
    private SongService songService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<AppUser> users = userService.findAllUsers();
        users.removeIf(user -> user.getAppUserRole().toString().equals("ADMIN"));
        model.addAttribute("users", users);
        return "admin_users";
    }

    @GetMapping("/songs")
    public String listSongs(Model model) {
        model.addAttribute("songs", songService.findAllSongs());
        return "admin_songs";
    }

    @DeleteMapping("/deleteUser/{userId}")
    @ResponseBody
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User deleted successfully";
    }

    @PutMapping("/disableUser/{userId}")
    @ResponseBody
    public String disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return "User disabled successfully";
    }

    @PutMapping("/enableUser/{userId}")
    @ResponseBody
    public String enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return "User enabled successfully";
    }

    @PutMapping("/toggleUserStatus/{userId}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        return userService.toggleUserStatus(userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DeleteMapping("/deleteSong/{songId}")
    @ResponseBody
    public ResponseEntity<?> deleteSong(@PathVariable Long songId) {
       return songService.deleteSongAdmin(songId);
       // return "Song deleted successfully";
    }
}
