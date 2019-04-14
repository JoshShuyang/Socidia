package socidia.middleware_server.repository;

import socidia.middleware_server.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long>{
    Optional<EmailVerificationToken> findByToken(String VerificationToken);
    void deleteByUserId(long user_id);
}
