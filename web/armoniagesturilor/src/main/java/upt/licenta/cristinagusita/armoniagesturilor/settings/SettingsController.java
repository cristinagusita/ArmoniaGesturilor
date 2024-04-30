package upt.licenta.cristinagusita.armoniagesturilor.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;

import javax.validation.Valid;
@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private AppUserService appUserService;
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @GetMapping
    public String showSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((AppUser) auth.getPrincipal()).getEmail();
        AppUser user = (AppUser) appUserService.loadUserByUsername(email);
        model.addAttribute("user", user);
        return "settings";
    }


//    @PostMapping
//    public String updateSettings(@ModelAttribute("user") @Valid AppUser userForm) {
//        AppUser currentUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        appUserService.updateUser(currentUser.getEmail(), userForm.getFirstName(), userForm.getLastName(), userForm.getEmail());
//        return "redirect:/settings?success";
//    }

    @PostMapping
    public String updateSettings(@ModelAttribute("user") @Valid AppUser userForm) {

        System.out.println("Entered updateSettings method");

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = (AppUser) currentAuth.getPrincipal();

        // Update user details in the database
        appUserService.updateUser(currentUser.getEmail(), userForm.getFirstName(), userForm.getLastName(), userForm.getEmail());

        // Fetch updated user details from the database
        AppUser updatedUser = (AppUser) appUserService.loadUserByUsername(userForm.getEmail());

        // Create a new Authentication with updated details and set it in the context
        Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUser, currentAuth.getCredentials(), currentAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/settings?success";
    }


    @PostMapping("/change-password")
    public String changeUserPassword(Authentication authentication,
                                     @RequestParam String currentPassword,
                                     @RequestParam String newPassword,
                                     @RequestParam String confirmNewPassword,
                                     RedirectAttributes redirectAttributes) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        System.out.println("Entered changeuserpass method");

        if (!newPassword.equals(confirmNewPassword)) {
            System.out.println("New password and confirm password didn't match");
            redirectAttributes.addFlashAttribute("error", "New passwords must match!");
            return "redirect:/settings#change-password";
        }


        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(currentPassword, currentUser.getPassword())) {
            System.out.println("Current pass is incorrect");
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
            return "redirect:/settings#change-password";
        }

        appUserService.changePassword(currentUser, bCryptPasswordEncoder.encode(newPassword));
        System.out.println("Changed pass successfully");
        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/settings#change-password";
    }

}


