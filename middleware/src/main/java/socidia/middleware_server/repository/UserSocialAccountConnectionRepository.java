package socidia.middleware_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import socidia.middleware_server.model.UserSocialAccountConnection;

import java.util.Optional;

public interface UserSocialAccountConnectionRepository extends JpaRepository<UserSocialAccountConnection, Long>{
    //Optional<EmailVerificationToken> findByToken(String VerificationToken);
}
