package socidia.middleware_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;


public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Log LOG = LogFactory.getLog(CustomAuthenticationFilter.class);

    private static final String ERROR_MESSAGE = "Something went wrong while parsing /login request body";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationFilter() {
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String requestBody;
        try {
            requestBody = IOUtils.toString(request.getReader());

            LoginRequest authRequest = objectMapper.readValue(requestBody, LoginRequest.class);
            System.out.println(authRequest);
            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password);

            // Allow subclasses to set the "details" property
            setDetails(request, token);

            return this.getAuthenticationManager().authenticate(token);
        } catch(IOException e) {
            LOG.error(ERROR_MESSAGE, e);
            //throw new InternalAuthenticationServiceException(ERROR_MESSAGE, e);
            return null;
        }
    }
    public static class LoginRequest {
        String username;
        String password;

        public LoginRequest() {
        }

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}