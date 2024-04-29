package upt.licenta.cristinagusita.armoniagesturilor.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upt.licenta.cristinagusita.armoniagesturilor.registration.RegistrationRequest;

@Controller
@AllArgsConstructor
public class RegistrationWebController {

    private final RegistrationService registrationService;

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/api/v1/registration")
    public String registerUserAccount(@ModelAttribute("registrationRequest") RegistrationRequest registrationRequest,
                                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Validation errors occurred!");
            return "register";
        }
        try {
            registrationService.register(registrationRequest);
            model.addAttribute("successMessage", "Registration successful! Check your email to confirm your registration.");
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "register";
    }

}
