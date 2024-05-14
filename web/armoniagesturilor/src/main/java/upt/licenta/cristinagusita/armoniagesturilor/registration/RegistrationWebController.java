package upt.licenta.cristinagusita.armoniagesturilor.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
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
            model.addAttribute("errorMessage", "Au apărut erori de validare!");
            return "register";
        }
        try {
            registrationService.register(registrationRequest);
            model.addAttribute("successMessage", "Te-ai înregistrat cu succes! Verifică-ți emailul pentru confirmarea contului!");
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "register";
    }

    @GetMapping(path = "/registration/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        try {
            String message = registrationService.confirmToken(token);
            model.addAttribute("successMessage", message);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "confirm";
    }

    @GetMapping(path = "/update-email/confirm")
    public String confirmEmailUpdate(@RequestParam("token") String token, Model model) {
        try {
            String message = registrationService.confirmUpdateToken(token);
            model.addAttribute("successMessage", "Email actualizat cu succes! Te rugăm să te conectezi cu noul email. " + message);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "email-update-confirm";
    }

}
