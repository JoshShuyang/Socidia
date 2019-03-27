
package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;
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
import java.util.HashMap;
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
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public HashMap<String, String> signup(@RequestBody UserSignup userSignup, HttpServletRequest request) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        Locale locale = request.getLocale();
        String username = userSignup.getUsername();
        String email = userSignup.getEmail();
        String password = userSignup.getPassword();

        Optional<User> registered = userRepository.findByEmail(email);
        if (registered.isPresent()) {
            System.out.println(registered + " This email has already existed!");
            // email address has existed 409
            //return new ResponseEntity<>(" This email " + email + " has already existed!", HttpStatus.CONFLICT);
            final String messageError = messages.getMessage("message.regError", null, locale);
            returnJsonPair.put("error", messageError);
            return returnJsonPair;
        }

        User res = userService.register(username, email, password);
        if (res != null) {
            try {
                String appUrl = request.getScheme() + request.getServerName() + request.getServerPort() + request.getContextPath();
                eventPublisher.publishEvent(new OnRegistrationSuccessEvent(res, request.getLocale(), appUrl));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("IO Error while auto login after register " + e);
                // failed sending verification email 403
                //return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                final String messageError = messages.getMessage("message.badEmail", null, locale);
                returnJsonPair.put("error", messageError);
                return returnJsonPair;
            }
            //return new ResponseEntity<>(HttpStatus.OK);
            final String message = messages.getMessage("message.regSucc", null, locale);
            returnJsonPair.put("success", message);
            return returnJsonPair;
        }
        // 422
        //return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        final String messageError = messages.getMessage("message.regUnknownError", null, locale);
        returnJsonPair.put("error", messageError);
        return returnJsonPair;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration
            (HttpServletRequest request, @RequestParam("token") String token) {
        Locale locale = request.getLocale();
        ModelAndView modelAndView = new ModelAndView();

        EmailVerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            // invalid token 403
            //return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            modelAndView.addObject("token", token);
            final String messageError = messages.getMessage("message.invalidToken", null, locale);
            modelAndView.addObject("message", messageError);
            return modelAndView;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            // token expired 499
            //return ResponseEntity.status(499).body(" token expired");
            modelAndView.addObject("token", token);
            final String messageError = messages.getMessage("message.expired", null, locale);
            modelAndView.addObject("message", messageError);
            return modelAndView;
        }

        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        modelAndView.addObject("email", user.getEmail());
        final String message = messages.getMessage("message.actSucc", null, locale);
        modelAndView.addObject("message", message);
        modelAndView.setViewName("success");
        //return new ResponseEntity<>(HttpStatus.OK);
        return modelAndView;
    }
}
