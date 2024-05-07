package upt.licenta.cristinagusita.armoniagesturilor.sing;


import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.song.Song;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class SingController {
    private AppUserService appUserService;

// logic for the user to always be logged in

//    @GetMapping(path = "/canta")
//    public String get(Model model, Principal principal) {
//        AppUser user = (AppUser) appUserService.loadUserByUsername(principal.getName());
//        model.addAttribute("username", user.getUsername());
//        return "canta";
//    }

//    @GetMapping(path = "/canta")
//    public String get(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
//            // User is logged in
//            AppUser user = (AppUser) appUserService.loadUserByUsername(auth.getName());
//            model.addAttribute("username", user.getUsername());
//        }
//
//        return "canta";
//    }
@GetMapping(path = "/canta")
public String get(Model model, Principal principal) {
    if (principal != null) {
        // User is authenticated, proceed as normal
        AppUser user = (AppUser) appUserService.loadUserByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());
    } else {
        // No user is authenticated
        model.addAttribute("username", "Guest");
    }
    return "canta";
}
}
