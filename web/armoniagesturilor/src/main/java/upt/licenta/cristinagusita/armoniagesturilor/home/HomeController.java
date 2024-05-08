package upt.licenta.cristinagusita.armoniagesturilor.home;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;

@Controller
@AllArgsConstructor
public class HomeController {
    private AppUserService appUserService;

//    @GetMapping(path = "/home")
//    public String get() {
//        return "home";
//    }

    @GetMapping(path = "/")
    public String get() {
        return "home";
    }
}
