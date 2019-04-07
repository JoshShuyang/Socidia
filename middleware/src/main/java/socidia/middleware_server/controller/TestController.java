package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import socidia.middleware_server.event.OnRegistrationSuccessEvent;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@RestController
public class TestController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;
    @Autowired
    private Environment env;

    @RequestMapping("/hello")
    public String hello() {
        String s = env.getProperty("support.email");
        System.out.println(s);
        return "Hello world!";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ResponseEntity<?> signupTest(HttpServletRequest request) {

        String username = "bob";
        String email = "na.yue@sjsu.edu";
        String password = "123";

        Optional<User> op = userRepository.findByEmail(email);
        User res = op.get();
        if (res != null) {
            try {
                String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                eventPublisher.publishEvent(new OnRegistrationSuccessEvent(res, request.getLocale(), appUrl));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("IO Error while auto login after register " + e);
                // failed sending verification email 403
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        // 422
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping("/page")
    public HashMap<String, String> index (HttpServletRequest request) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        Locale locale = request.getLocale();
        final String message = messages.getMessage("message.regSucc", null, locale);
        returnJsonPair.put("success", message);
        return returnJsonPair;
    }
}
