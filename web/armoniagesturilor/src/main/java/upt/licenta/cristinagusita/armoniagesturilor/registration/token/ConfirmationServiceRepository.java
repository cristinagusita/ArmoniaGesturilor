package upt.licenta.cristinagusita.armoniagesturilor.registration.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationServiceRepository extends JpaRepository<ConfirmationToken, Long> {

    //optional because it may or may not find a token
    Optional<ConfirmationToken> findByToken(String token);
    List<ConfirmationToken> findByAppUserEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmedAt(String token, java.time.LocalDateTime confirmedAt);

    //List<ConfirmationToken> findByEmail(String email);
}
