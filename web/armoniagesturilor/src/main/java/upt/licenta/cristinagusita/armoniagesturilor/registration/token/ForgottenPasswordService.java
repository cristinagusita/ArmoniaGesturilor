package upt.licenta.cristinagusita.armoniagesturilor.registration.token;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;
import upt.licenta.cristinagusita.armoniagesturilor.email.EmailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ForgottenPasswordService {

    private final AppUserService appUserService;
    private final ForgottenPasswordRepository forgottenPasswordRepository;
    private final EmailSender emailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(ForgottenPasswordService.class);

    @Transactional
    public void saveForgottenPasswordToken(ForgottenPasswordToken token) {
        logger.info("Saving new token for user: {}", token.getAppUser().getEmail());
        ForgottenPasswordToken savedToken = forgottenPasswordRepository.save(token);
        if (savedToken != null && savedToken.getId() != null) {
            logger.info("Token saved with ID: {}", savedToken.getId());
        } else {
            logger.error("Failed to save token for user: {}", token.getAppUser().getEmail());
        }
    }


    public Optional<ForgottenPasswordToken> getToken(String token) {
        return forgottenPasswordRepository.findByToken(token);
    }

    @Transactional
    @Modifying
    public int setConfirmedAt(String token) {
        return forgottenPasswordRepository.updateConfirmedAt(
                token, java.time.LocalDateTime.now());
    }

    public void saveToken(ForgottenPasswordToken forgottenPasswordToken) {
        forgottenPasswordRepository.save(forgottenPasswordToken);
    }

    @Transactional
    public void updateToken(ForgottenPasswordToken forgottenPasswordToken) {
        ForgottenPasswordToken token = forgottenPasswordRepository.findByToken(forgottenPasswordToken.getToken()).get();
        token.setExpiresAt(forgottenPasswordToken.getExpiresAt());
    }

    @Transactional
    public void invalidateOldToken(String email) {
        List<ForgottenPasswordToken> tokens = forgottenPasswordRepository.findByAppUserEmail(email);
        for (ForgottenPasswordToken token : tokens) {
            token.setConfirmedAt(LocalDateTime.now());
            forgottenPasswordRepository.save(token); // Persist each updated token
        }
    }

    @Transactional
    public String createAndSaveToken(AppUser appUser) {
        invalidateOldToken(appUser.getEmail()); // Ensure old tokens are invalidated
        String token = UUID.randomUUID().toString();
        ForgottenPasswordToken forgottenPasswordToken = new ForgottenPasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        saveForgottenPasswordToken(forgottenPasswordToken);
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        forgottenPasswordRepository.deleteByAppUserId(userId);
    }

    public void sendForgottenPasswordEmail(AppUser user) {
        String token = createAndSaveToken(user);
        String link = "http://localhost:8081/forgot-password?token=" + token;
        String email = user.getEmail();
        String name = user.getFirstName();
        String emailContent = buildForgotPasswordEmail(name, link);
        emailSender.send(email, emailContent, "Reset your password");
    }

    private String buildForgotPasswordEmail(String name, String link) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Resetare Parolă</span>\n" +
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
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Bună " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Ai solicitat resetarea parolei. Apasă pe link-ul de mai jos pentru a-ți reseta parola: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Resetează parola</a> </p></blockquote>\n Link-ul va expira în 15 minute. <p>Mulțumim!</p>" +
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
    @Transactional
    public void updatePassword(String token, String password) {
        Optional<ForgottenPasswordToken> forgottenPasswordToken = forgottenPasswordRepository.findByToken(token);
        if (forgottenPasswordToken.isPresent()) {
            // Check if token is expired
            if (forgottenPasswordToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Token expired");
            }
            AppUser appUser = forgottenPasswordToken.get().getAppUser();
            String encodedPassword = bCryptPasswordEncoder.encode(password);
            // check if password is same as current password
            if (bCryptPasswordEncoder.matches(password, appUser.getPassword())) {
                throw new IllegalStateException("Parola nouă nu poate fi aceeași cu cea veche!");
            }
            appUserService.changePassword(appUser, encodedPassword);
            forgottenPasswordRepository.delete(forgottenPasswordToken.get());
        } else {
            throw new IllegalStateException("Token not found");
        }
    }

    public boolean checkIfTokenExpired(String token) {
        Optional<ForgottenPasswordToken> forgottenPasswordToken = forgottenPasswordRepository.findByToken(token);
        return forgottenPasswordToken.isEmpty() || forgottenPasswordToken.get().getExpiresAt().isBefore(LocalDateTime.now());
    }
}
