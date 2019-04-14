package socidia.middleware_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import socidia.middleware_server.model.UserSocialAccountConnection;

import java.util.List;
import java.util.Optional;

public interface UserSocialAccountConnectionRepository extends JpaRepository<UserSocialAccountConnection, Long>{
    public static final String FIND_CONNECTION = "SELECT * FROM user_social_account_connection c " +
            "whehe c.provider_id=?1 and c.provider_user_id=?2 and c.user_id=?3 and c.delete=false" +
            "order by c.expire_time desc";

    List<UserSocialAccountConnection> findByUserId(long user_id);

    @Query(value = FIND_CONNECTION, nativeQuery = true)
    List<UserSocialAccountConnection> findByProviderUserId(String providerId, String providerUserId, String userId);

    void deleteByUserId(long user_id);
}
