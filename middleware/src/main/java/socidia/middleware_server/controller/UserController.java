package socidia.middleware_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import socidia.middleware_server.dto.UserEdit;
import socidia.middleware_server.dto.UserSignup;
import socidia.middleware_server.model.User;
import socidia.middleware_server.repository.RegistrationVerificationTokenRepository;
import socidia.middleware_server.repository.RoleRepository;
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

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping(value = "/user/edit", method = RequestMethod.PUT)
    public HashMap<String, String> editUser(@RequestBody UserEdit userEdit) {
        HashMap<String, String> returnJsonPair = new HashMap<>();
        User user = userRepository.findByEmail(userEdit.getEmail()).get();
        if (!userEdit.getUsername().isEmpty()) {
            user.setUsername(userEdit.getUsername());
        }
        if (!userEdit.getPassword().isEmpty()) {
            user.setPassword(userEdit.getPassword());
        }
        if (!userEdit.getPhoneNum().isEmpty()) {
            user.setPhoneNum(userEdit.getPhoneNum());
        }

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
        //connectionRepository.deleteByUserId(id);
        //tokenRepository.deleteByUserId(id);
        //roleRepository.deleteByUserId(id);
        userRepository.deleteById(id);
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
