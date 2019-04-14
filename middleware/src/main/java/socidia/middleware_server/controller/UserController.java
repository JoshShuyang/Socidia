package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import socidia.middleware_server.dto.UserEdit;
import socidia.middleware_server.dto.UserSignup;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.RegistrationVerificationTokenRepository;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.repository.UserSocialAccountConnectionRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/middleware")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSocialAccountConnectionRepository connectionRepository;

    @Autowired
    private RegistrationVerificationTokenRepository tokenRepository;

    @RequestMapping(value = "/user/edit", method = RequestMethod.PUT)
    public HashMap<String, String> editUser(@RequestBody UserEdit userEidt) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        User user = userRepository.findByEmail(userEidt.getEmail()).get();
        user.setPassword(userEidt.getPassword());
        user.setUsername(userEidt.getUsername());
        user.setPhoneNum(userEidt.getPhoneNum());
        User res = userRepository.save(user);
        if (res != null) {
            returnJsonPair.put("success", "user info edits successfully");
        } else {
            returnJsonPair.put("error", "user info edit failure");
        }

        return returnJsonPair;
    }

    @RequestMapping(value = "/user/delete/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(value="id") long id) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        userRepository.deleteById(id);
        connectionRepository.deleteByUserId(id);
        tokenRepository.deleteByUserId(id);
    }

    @RequestMapping(value = "/user/disable/{id}", method = RequestMethod.PUT)
    public HashMap<String, String> disableUser(@PathVariable(value="id") long id) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        User user = userRepository.findById(id).get();
        if (user != null) {
            user.setEnabled(false);
            userRepository.save(user);
            returnJsonPair.put("success", user.getEmail() + " was disabled");
        } else {
            returnJsonPair.put("error", "disabe operation failed");
        }
        return returnJsonPair;
    }
}
