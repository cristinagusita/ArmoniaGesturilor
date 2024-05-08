package upt.licenta.cristinagusita.armoniagesturilor.discover;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.song.SongService;
import upt.licenta.cristinagusita.armoniagesturilor.song.Song;

import java.security.Principal;

import java.util.List;

@Controller
@AllArgsConstructor
public class DiscoverController {
    private AppUserService appUserService;
    private SongService songService;

    @GetMapping(path = "/descopera")
    public String get(Model model, Principal principal) {
//        AppUser user = (AppUser) appUserService.loadUserByUsername(principal.getName());
        List<Song> songs = songService.findAll();
        model.addAttribute("songs", songs);
        return "descopera";
    }
}