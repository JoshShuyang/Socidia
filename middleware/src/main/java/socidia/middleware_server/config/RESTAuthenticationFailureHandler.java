package socidia.middleware_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import socidia.middleware_server.service.CustomUserDetailsService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class RESTAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;
    @Autowired
    private LocaleResolver localeResolver;
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        //super.onAuthenticationFailure(request, response, exception);
        ObjectMapper objectMapper = new ObjectMapper();
        Locale locale = localeResolver.resolveLocale(request);
        String errorMessage = messages.getMessage("message.badCredentials", null, locale);

        if (exception.getMessage().equalsIgnoreCase("Email address doesn't exist")) {
            errorMessage = messages.getMessage("auth.message.emailNotExist", null, locale);
        } else if (exception.getMessage().equalsIgnoreCase("unactivated")) {
            errorMessage = messages.getMessage("auth.message.disabled", null, locale);
        }

        response.setStatus(HttpStatus.OK.value());
        HashMap<String, String> returnJsonPair = new HashMap<>();
        returnJsonPair.put("error", errorMessage);
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(returnJsonPair));

    }
}