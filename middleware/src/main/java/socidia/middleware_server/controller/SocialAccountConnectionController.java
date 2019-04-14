
package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import socidia.middleware_server.dto.ConnectionRevoked;
import socidia.middleware_server.dto.UserSignup;
import socidia.middleware_server.event.OnRegistrationSuccessEvent;
import socidia.middleware_server.model.EmailVerificationToken;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.repository.UserSocialAccountConnectionRepository;
import socidia.middleware_server.service.FacebookService;
import socidia.middleware_server.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/middleware")
public class SocialAccountConnectionController {

    @Autowired
    FacebookService facebookService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSocialAccountConnectionRepository userSocialAccountConnectionRepository;

    @GetMapping("/createFacebookAuthorization")
    public HashMap<String, String> createFacebookAuthorization(){
        HashMap<String, String> returnJsonPair = new HashMap<>();
        String oauthURL = facebookService.createFacebookAuthorizationURL();
        returnJsonPair.put("oauthURL", oauthURL);
        return returnJsonPair;
    }

    @GetMapping("/facebook")
    public HashMap<String, String> createFacebookAccessToken(@RequestParam("code") Optional<String> code,
                                          @RequestParam("error") Optional<String> error,
                                          @RequestParam("error_code") Optional<String> error_code,
                                          @RequestParam("error_description") Optional<String> error_description,
                                          @RequestParam("error_reason") Optional<String> error_reason){
        HashMap<String, String> returnJsonPair = new HashMap<>();
        if (code.isPresent()) {
            facebookService.createFacebookAccessToken(code.get());
            returnJsonPair.put("success", "authorization granted");
        } else {
            returnJsonPair.put("error", error_reason.get());
        }
        return returnJsonPair;
    }

    // getSocialAccount
    // userId providerId providerUserId
    @GetMapping("/getSocialAccount")
    public List<?> getSocialAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepository.findByEmail(username).get();
        return userSocialAccountConnectionRepository.findByUserId(user.getId());
    }

    @RequestMapping(value ="/revokeOauth", method = RequestMethod.POST)
    public boolean revokeOauth(@RequestBody ConnectionRevoked connection) {
        return facebookService.revokePermission(connection.getProviderId(), connection.getProviderUserId(), connection.getUserId());
    }

}
