//package upt.licenta.cristinagusita.armoniagesturilor.registration.token;
//
//import lombok.AllArgsConstructor;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@AllArgsConstructor
//public class ConfirmationTokenService {
//
//    private final ConfirmationServiceRepository confirmationServiceRepository;
//
//    public void saveConfirmationToken(ConfirmationToken token) {
//        confirmationServiceRepository.save(token);
//    }
//
//    public Optional<ConfirmationToken> getToken(String token) {
//        return confirmationServiceRepository.findByToken(token);
//    }
//
//    @Transactional
//    @Modifying
//    public int setConfirmedAt(String token) {
//        return confirmationServiceRepository.updateConfirmedAt(
//                token, java.time.LocalDateTime.now());
//    }
//
//    public void saveToken(ConfirmationToken confirmationToken) {
//        confirmationServiceRepository.save(confirmationToken);
//    }
//
//    @Transactional
//    public void updateToken(ConfirmationToken confirmationToken) {
//        ConfirmationToken token = confirmationServiceRepository.findByToken(confirmationToken.getToken()).get();
//        token.setExpiresAt(confirmationToken.getExpiresAt());
//    }
//}


package upt.licenta.cristinagusita.armoniagesturilor.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationServiceRepository confirmationServiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(ConfirmationTokenService.class);

    @Transactional
    public void saveConfirmationToken(ConfirmationToken token) {
        logger.info("Saving new token for user: {}", token.getAppUser().getEmail());
        ConfirmationToken savedToken = confirmationServiceRepository.save(token);
        if (savedToken != null && savedToken.getId() != null) {
            logger.info("Token saved with ID: {}", savedToken.getId());
        } else {
            logger.error("Failed to save token for user: {}", token.getAppUser().getEmail());
        }
    }


    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationServiceRepository.findByToken(token);
    }

    @Transactional
    @Modifying
    public int setConfirmedAt(String token) {
        return confirmationServiceRepository.updateConfirmedAt(
                token, java.time.LocalDateTime.now());
    }

    public void saveToken(ConfirmationToken confirmationToken) {
        confirmationServiceRepository.save(confirmationToken);
    }

    @Transactional
    public void updateToken(ConfirmationToken confirmationToken) {
        ConfirmationToken token = confirmationServiceRepository.findByToken(confirmationToken.getToken()).get();
        token.setExpiresAt(confirmationToken.getExpiresAt());
    }

    @Transactional
    public void invalidateOldToken(String email) {
        List<ConfirmationToken> tokens = confirmationServiceRepository.findByAppUserEmail(email);
        for (ConfirmationToken token : tokens) {
            token.setConfirmedAt(LocalDateTime.now());
            confirmationServiceRepository.save(token); // Persist each updated token
        }
    }

    @Transactional
    public String createAndSaveToken(AppUser appUser) {
        invalidateOldToken(appUser.getEmail()); // Ensure old tokens are invalidated
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        saveConfirmationToken(confirmationToken);
        return token;
    }

    @Transactional
    public String createAndSavePendingEmailToken(AppUser existingUser) {
        invalidateOldToken(existingUser.getEmail()); // Ensure old tokens are invalidated
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                existingUser
        );
        saveConfirmationToken(confirmationToken);
        existingUser.setPendingEmailToken(token);
        return token;
    }
}
