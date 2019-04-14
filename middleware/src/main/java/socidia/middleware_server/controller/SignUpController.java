
package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ModelAndView;
import socidia.middleware_server.dto.UserSignup;
import socidia.middleware_server.event.OnRegistrationSuccessEvent;
import socidia.middleware_server.model.EmailVerificationToken;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.RegistrationVerificationTokenRepository;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping(value = "/middleware")
public class SignUpController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationVerificationTokenRepository registrationVerificationTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;

    @Autowired
    private Environment env;

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
            return sendConfirmEmail(request, returnJsonPair, locale, res);
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
        SignupConfirmErrorModelObject errorModelObject = new SignupConfirmErrorModelObject();
        errorModelObject.setToken(token);

        EmailVerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            // invalid token 403
            //return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            errorModelObject.setCaseNum("403");
            final String messageError = messages.getMessage("message.invalidToken", null, locale);
            errorModelObject.setMessage(messageError);
            errorModelObject.setLink(env.getProperty("server.homepage"));
            errorModelObject.setLinkMessage("go to homepage to login");
            modelAndView.addObject("error", errorModelObject);

            modelAndView.setViewName("error");
            return modelAndView;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            // token expired 499
            //return ResponseEntity.status(499).body(" token expired");
            errorModelObject.setCaseNum("499");
            final String messageError = messages.getMessage("message.expired", null, locale);
            errorModelObject.setMessage(messageError);
            errorModelObject.setLink(env.getProperty("server.homepage") + "/reapplyToken/");
            errorModelObject.setLinkMessage("Reapply confirmation token to activate your account!");
            modelAndView.addObject("error", errorModelObject);

            modelAndView.setViewName("error");
            return modelAndView;
        }

        if (verificationToken.isUsed()) {
            // token has been used
            errorModelObject.setCaseNum("403");
            final String messageError = messages.getMessage("message.used", null, locale);
            errorModelObject.setMessage(messageError);
            errorModelObject.setLink(env.getProperty("server.homepage"));
            errorModelObject.setLinkMessage("go to homepage to login");
            modelAndView.addObject("error", errorModelObject);

            modelAndView.setViewName("error");
            return modelAndView;
        }

        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationToken.setUsed(true);
        registrationVerificationTokenRepository.save(verificationToken);
        modelAndView.addObject("username", user.getEmail());
        final String message = messages.getMessage("message.actSucc", null, locale);
        modelAndView.addObject("message", message);
        modelAndView.addObject("link", env.getProperty("server.homepage"));
        modelAndView.setViewName("success");
        //return new ResponseEntity<>(HttpStatus.OK);
        return modelAndView;
    }

    @RequestMapping(value = "/reapplyToke", method = RequestMethod.GET)
    public HashMap<String, String> reapplyToke(HttpServletRequest request, @RequestParam("token") String token) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        Locale locale = request.getLocale();
        EmailVerificationToken verificationToken = userService.getVerificationToken(token);
        verificationToken.setUsed(true);
        registrationVerificationTokenRepository.save(verificationToken);
        User user = verificationToken.getUser();

        return sendConfirmEmail(request, returnJsonPair, locale, user);
    }

    private HashMap<String, String> sendConfirmEmail(HttpServletRequest request, HashMap<String, String> returnJsonPair, Locale locale, User user) {
        try {
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationSuccessEvent(user, request.getLocale(), appUrl));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IO Error while auto login after register " + e);
            // failed sending verification email 403
            //return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            final String messageError = messages.getMessage("message.badEmail", null, locale);
            returnJsonPair.put("error", messageError);
            return returnJsonPair;

        }
        final String message = messages.getMessage("message.regSucc", null, locale);
        returnJsonPair.put("success", message);
        return returnJsonPair;
    }

    public static class SignupConfirmErrorModelObject {
        private String token;
        private String message;
        private String link;
        private String linkMessage;
        private String caseNum;

        public SignupConfirmErrorModelObject() {
        }

        public SignupConfirmErrorModelObject(String token, String message, String link, String linkMessage, String caseNum) {
            this.token = token;
            this.message = message;
            this.link = link;
            this.linkMessage = linkMessage;
            this.caseNum = caseNum;
        }

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLinkMessage() {
            return linkMessage;
        }

        public void setLinkMessage(String linkMessage) {
            this.linkMessage = linkMessage;
        }

        public String getCaseNum() {
            return caseNum;
        }

        public void setCaseNum(String caseNum) {
            this.caseNum = caseNum;
        }
    }
}
