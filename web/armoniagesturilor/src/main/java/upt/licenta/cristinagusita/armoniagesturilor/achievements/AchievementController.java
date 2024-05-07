package upt.licenta.cristinagusita.armoniagesturilor.achievements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.song.SongService;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;
    @Autowired
    private AppUserService userService;

    @GetMapping("/check/{username}")
    public ResponseEntity<?> checkAndAssignAchievements(@PathVariable String username) {

        AppUser user = (AppUser) userService.loadUserByUsername(username);
        String achievementAssigned = achievementService.checkAndAssignAchievements(user);
        return ResponseEntity.ok().body(achievementAssigned);
    }
    @GetMapping("/profile/{username}")
    public String getUserProfile(@PathVariable String username, Model model) {
        AppUser user = (AppUser) userService.loadUserByUsername(username);
        model.addAttribute("songs", user.getSongs());
        model.addAttribute("achievements", user.getAchievements());
        return "profile";
    }
}
