package socialmediaprotection.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import socialmediaprotection.project.dataRepository.UserInsideInfoRepository;
import socialmediaprotection.project.entities.UserInsideInfo;

import java.util.List;

@RestController
public class UserInsideInfoController {
    final static Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    UserInsideInfoRepository userInsideInfoRepository;

    @GetMapping("/{userId}/userinsideinfo")
    public List<UserInsideInfo> getItemsbyUserId(@PathVariable String userId){
        int user_id = Integer.parseInt(userId);
        logger.info("Get UserInsideInfo for user_id: " + userId);
        return userInsideInfoRepository.findByUser_id(user_id);
    }
}
