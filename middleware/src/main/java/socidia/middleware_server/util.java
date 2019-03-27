package socidia.middleware_server;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class util {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String encoded = passwordEncoder.encode("123").toString();

        System.out.println(encoded);
    }
}
