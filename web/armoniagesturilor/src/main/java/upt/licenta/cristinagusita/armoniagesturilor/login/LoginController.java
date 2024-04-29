package upt.licenta.cristinagusita.armoniagesturilor.login;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String getLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password!");
        }
        return "login";
    }

    @PostMapping("/api/v1/login")
    public String login(@ModelAttribute("email") String email,
                        @ModelAttribute("password") String password,
                        Model model) {
        try {
            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(authReq);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/profile";  // Redirect to the user profile or another appropriate page
        } catch (BadCredentialsException e) {
            model.addAttribute("errorMessage", "Invalid email or password");
            return "login";  // Stay on the login page
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred");
            return "login";  // Stay on the login page
        }
    }
}
