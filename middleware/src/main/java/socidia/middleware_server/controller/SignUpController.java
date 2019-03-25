
package socidia.middleware_server.controller;

import socidia.middleware_server.dto.UserSignup;
import socidia.middleware_server.event.OnRegistrationSuccessEvent;
import socidia.middleware_server.model.EmailVerificationToken;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

@RestController
public class SignUpController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody UserSignup userSignup, HttpServletRequest request) {
        System.out.println("Sign Up API reached!");
        return new ResponseEntity<>(HttpStatus.OK);
        // ***********************************
        String username = userSignup.getUsername();
        String email = userSignup.getEmail();
        String password = userSignup.getPassword();

        Optional<User> registered = userRepository.findByEmail(email);
        if (registered.isPresent()) {
            System.out.println(registered + " This email has already existed!");
            // email address has existed 409
            return new ResponseEntity<>(" This email " + email + " has already existed!", HttpStatus.CONFLICT);
        }

        User res = userService.register(username, email, password);
        if (res != null) {
            String token = null;
            try {
                String appUrl = request.getContextPath();
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

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirmRegistration
            (HttpServletRequest request, @RequestParam("token") String token) {

        Locale locale = request.getLocale();

        EmailVerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            // invalid token 403
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            // token expired 499
            return ResponseEntity.status(499).body(" token expired");
        }

        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ResponseEntity<?> signupTest(HttpServletRequest request) {

        String username = "bob";
        String email = "na.yue@sjsu.edu";
        String password = "123";

        Optional<User> registered = userRepository.findByEmail(email);
        if (registered.isPresent()) {
            System.out.println(registered + " This email has already existed!");
            // email address has existed 409
            return new ResponseEntity<>(" This email " + email + " has already existed!", HttpStatus.CONFLICT);
        }

        User res = userService.register(username, email, password);
        if (res != null) {
            String token = null;
            try {
                String appUrl = request.getContextPath();
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

}
