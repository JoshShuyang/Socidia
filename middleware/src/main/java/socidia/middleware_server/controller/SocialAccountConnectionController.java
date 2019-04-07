
package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import socidia.middleware_server.dto.UserSignup;
import socidia.middleware_server.event.OnRegistrationSuccessEvent;
import socidia.middleware_server.model.EmailVerificationToken;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.service.FacebookService;
import socidia.middleware_server.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping(value = "/middleware")
public class SocialAccountConnectionController {

    @Autowired
    FacebookService facebookService;

    @GetMapping("/createFacebookAuthorization")
    public String createFacebookAuthorization(){
        return facebookService.createFacebookAuthorizationURL();
    }

    @GetMapping("/facebook")
    public void createFacebookAccessToken(@RequestParam("code") String code){
        facebookService.createFacebookAccessToken(code);
    }
}
