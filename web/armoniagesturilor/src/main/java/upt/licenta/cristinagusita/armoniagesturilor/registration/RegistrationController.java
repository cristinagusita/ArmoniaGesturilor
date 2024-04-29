package upt.licenta.cristinagusita.armoniagesturilor.registration;

import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.exception.SuccessMessage;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

//    @PostMapping(path = "/api/v1/registration")
//    public void register(@RequestBody RegistrationRequest request){
//        throw new SuccessMessage(registrationService.register(request));
//    }

//    @GetMapping(path = "/registration")
//    public String get(Model model) {
//        return "register";
//    }

    @GetMapping(path = "/registration/confirm")
    public void confirm(@RequestParam("token") String token) {
        throw new SuccessMessage(registrationService.confirmToken(token));
    }

    //    @GetMapping(path = "/update-email/confirm")
//    public void confirmEmail(@RequestParam("token") String token) {
//        throw new SuccessMessage(registrationService.confirmUpdateToken(token));
//    }
//    @GetMapping(path = "/update-email/confirm")
//    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
//        try {
//            String message = registrationService.confirmUpdateToken(token);
//            return ResponseEntity.ok(message);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
    @GetMapping(path = "/update-email/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
        try {
            String result = registrationService.confirmUpdateToken(token);
            // Invalidate session
            SecurityContextHolder.getContext().setAuthentication(null);
            SecurityContextHolder.clearContext();
            // Redirect or inform the user to log in with the new email
            return ResponseEntity.ok("Email updated successfully. Please log in with your new email.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
