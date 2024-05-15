package upt.licenta.cristinagusita.armoniagesturilor.login;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.registration.RegistrationRequest;
import upt.licenta.cristinagusita.armoniagesturilor.registration.token.ForgottenPasswordService;

@Controller
@AllArgsConstructor
public class ForgotPasswordController {
    private final AppUserService appUserService;
    private final ForgottenPasswordService forgottenPasswordService;

    @GetMapping("/forgot-password")
    public String forgotPassword(@RequestParam(name="token", defaultValue = "") String token, Model model) {
        // check if token String is bigger than 0
        if (!token.isEmpty()) {
            // check if token is valid
            if (forgottenPasswordService.getToken(token).isPresent()) {
                return "forgot-password-update";
            }
        }

        return "forgot-password-enter";
    }

    @PostMapping("/forgot-password")
    public String updatePassword(@RequestParam(name="token", defaultValue="") String token, @ModelAttribute("forgottenPasswordRequest") ForgottenPasswordRequest forgottenPasswordRequest, Model model) {
        if (token.isEmpty()) {
            String userEmail = forgottenPasswordRequest.getEmail();
            AppUser user;
            try {
                user = (AppUser) appUserService.loadUserByUsername(userEmail);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Email-ul nu există în baza de date!");
                return "forgot-password-enter";
            }

            forgottenPasswordService.sendForgottenPasswordEmail(user);
            return "forgot-password-email-sent";

        } else {
            // check if token is expired
            if (forgottenPasswordService.checkIfTokenExpired(token)) {
                return "forgot-password-enter";
            }
            // check if passwords are the same
            if (forgottenPasswordRequest.getPassword().equals(forgottenPasswordRequest.getConfirmPassword())) {
                try {
                    forgottenPasswordService.updatePassword(token, forgottenPasswordRequest.getPassword());
                } catch (Exception e) {
                    model.addAttribute("errorMessage", e.getMessage());
                    return "forgot-password-update";
                }

            } else {
                model.addAttribute("errorMessage", "Parolele nu coincid!");
                return "forgot-password-update";
            }
            return "forgot-password-success";
        }
    }
}
