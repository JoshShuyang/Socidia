package socidia.middleware_server.service;


import socidia.middleware_server.model.Role;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> userOp = userRepository.findByEmail(name);

        if (!userOp.isPresent()) {
            throw new RuntimeException("Email address doesn't exist");
        }
        User user = userOp.get();

        if (!user.isEnabled()) {
            throw new RuntimeException("unactivated");
        }

        Set<Role> roles = user.getRoles();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role: roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole_name()));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true, true, grantedAuthorities);
    }

}