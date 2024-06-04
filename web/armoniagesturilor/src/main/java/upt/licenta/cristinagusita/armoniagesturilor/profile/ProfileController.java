package upt.licenta.cristinagusita.armoniagesturilor.profile;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import upt.licenta.cristinagusita.armoniagesturilor.achievements.Achievement;
import upt.licenta.cristinagusita.armoniagesturilor.achievements.AchievementService;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.song.SongService;
import upt.licenta.cristinagusita.armoniagesturilor.song.Song;

import java.security.Principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
public class ProfileController {
    private AppUserService appUserService;
    private SongService songService;
    private AchievementService achievementService;

    @GetMapping(path = "/profile")
    public String get(Model model, Principal principal) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(principal.getName());
        List<Song> songs = songService.findByUserId(user.getId());
        songs.sort((s1, s2) -> s2.getId().compareTo(s1.getId()));
        List<Achievement> achievements = achievementService.getUserAchievements(user);
        List<String> achievementNames = achievementService.getAchievementNames(achievements);
        List<Achievement> allAchievements = achievementService.getAllAchievements();

        List<String> achievementImages = new ArrayList<String>(
                Arrays.asList(
                        "/images/badges/first_song.png",
                        "/images/badges/beginner.png",
                        "/images/badges/bronze.png",
                        "/images/badges/silver.png",
                        "/images/badges/gold.png",
                        "/images/badges/diamond.png"
                )
        );

        model.addAttribute("user", user);
        model.addAttribute("songs", songs);
        model.addAttribute("achievements", achievements);
        model.addAttribute("achievementNames", achievementNames);
        model.addAttribute("achievementImages", achievementImages);
        model.addAttribute("allAchievements", allAchievements);
        return "profile";
    }
}
