package socidia.middleware_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import socidia.middleware_server.model.User;
import socidia.middleware_server.model.UserSocialAccountConnection;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.repository.UserSocialAccountConnectionRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class RESTAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserSocialAccountConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        clearAuthenticationAttributes(request);
        response.setStatus(HttpStatus.OK.value());
        String username = authentication.getName();
        long user_id = userRepository.findByEmail(username).get().getId();
        List<UserSocialAccountConnection> list = connectionRepository.findByUserId(user_id);
        String json = new ObjectMapper().writeValueAsString(list);
        System.out.println(json);
        response.getWriter().write(json);
        response.flushBuffer();
    }
}