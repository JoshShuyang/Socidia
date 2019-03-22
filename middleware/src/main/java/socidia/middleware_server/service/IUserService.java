package socidia.middleware_server.service;

import socidia.middleware_server.model.EmailVerificationToken;
import socidia.middleware_server.model.User;

public interface IUserService {
    User register(String username, String email, String password);

    void createVerificationTokenForUser(User user, String token);

    EmailVerificationToken getVerificationToken(final String VerificationToken);
}
