package socidia.middleware_server.service;

import socidia.middleware_server.model.EmailVerificationToken;
import socidia.middleware_server.model.Role;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.RegistrationVerificationTokenRepository;
import socidia.middleware_server.repository.RoleRepository;
import socidia.middleware_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements IUserService {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RegistrationVerificationTokenRepository tokenRepository;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public User register(String username, String email, String password) {
        password = passwordEncoder.encode(password);
        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setEmailVerified(false);
        user.setPassword(password);
        //user.setRoles(new HashSet<>(Arrays.asList(new Role("ROLE_USER"))));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_USER"));
        user.setRoles(roles);
        user.setEnabled(false);

        User res = userRepository.save(user);

        return res;
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        final EmailVerificationToken myToken = new EmailVerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public EmailVerificationToken getVerificationToken(String VerificationToken) {
        Optional<EmailVerificationToken> token = tokenRepository.findByToken(VerificationToken);
        if (token.isPresent()) {
            return token.get();
        }
        return null;
    }


}
