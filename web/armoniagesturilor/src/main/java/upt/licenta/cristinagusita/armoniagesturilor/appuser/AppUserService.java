package upt.licenta.cristinagusita.armoniagesturilor.appuser;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upt.licenta.cristinagusita.armoniagesturilor.email.EmailSender;
import upt.licenta.cristinagusita.armoniagesturilor.exception.SuccessMessage;
import upt.licenta.cristinagusita.armoniagesturilor.registration.token.ConfirmationTokenService;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "User with email %s not found!";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return appUserRepository.findAppUserByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Password or email incorrect"));
    }

    @Transactional
    public String signUpUser(AppUser appUser) {
        Optional<AppUser> userOptional = appUserRepository.findAppUserByEmail(appUser.getEmail());
        if (userOptional.isPresent()) {
            AppUser existingUser = userOptional.get();
            if (!existingUser.getEnabled()) {
                String token = confirmationTokenService.createAndSaveToken(existingUser); // Handle token re-creation
                // Logic to send confirmation email again
                String link = "http://localhost:8081/registration/confirm?token=" + token;
                emailSender.send(existingUser.getEmail(), buildEmail(existingUser.getFirstName(), link));
                throw new SuccessMessage("Confirmation email sent again!");
            } else {
                throw new IllegalStateException("An account with this email already exists!");
            }
        }
        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));

        appUserRepository.save(appUser);
        String token = confirmationTokenService.createAndSaveToken(appUser); // Create token for new users
        // Logic to send confirmation email
        return token;
    }

    public void enableAppUser(String email) {
        appUserRepository.enableAppUser(email);
    }

    @Transactional
    public void updateUser(String currentEmail, String firstName, String lastName, String newEmail) {
        Optional<AppUser> userOptional = appUserRepository.findAppUserByEmail(currentEmail);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User not found!");
        }

        AppUser existingUser = userOptional.get();
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);

        if (!existingUser.getEmail().equals(newEmail) && (existingUser.getPendingEmail() == null || !existingUser.getPendingEmail().equals(newEmail))) {
            existingUser.setPendingEmail(newEmail);
            confirmationTokenService.createAndSavePendingEmailToken(existingUser);
            String mailToBeSent = buildEmail(existingUser.getFirstName(), "http://localhost:8081/update-email/confirm?token=" + existingUser.getPendingEmailToken());
            emailSender.send(newEmail, mailToBeSent);

        }

        appUserRepository.save(existingUser);
        // refresh the user in the security context
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }


    public Object isAuthenticatedUser() {
        return  SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
                //when Anonymous Authentication is enabled
//                !(SecurityContextHolder.getContext().getAuthentication()
//                        instanceof AnonymousAuthenticationToken);
    }

    public void updateEmail(String email, String pendingEmail) {
        Optional<AppUser> userOptional = appUserRepository.findAppUserByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User not found!");
        }

        AppUser existingUser = userOptional.get();
        existingUser.setEmail(pendingEmail);
        existingUser.setPendingEmail(null);
        existingUser.setPendingEmailToken(null);

        appUserRepository.save(existingUser);
    }


    public void changePassword(AppUser user, String encodedPassword) {
        user.setPassword(encodedPassword);
        appUserRepository.save(user);
    }

}