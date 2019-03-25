package socialmediaprotection.project.controllers;

import socialmediaprotection.project.dataRepository.PolicyRepository;
import socialmediaprotection.project.entities.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PolicyController {

    final static Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    PolicyRepository policyRepository;

    @GetMapping("/{userId}/policy")
    public List<Policy> getPolicybyUserId(@PathVariable String userId){
        int user_id = Integer.parseInt(userId);
        logger.info("Get policy for user_id: " + userId);
        return policyRepository.findByUser_id(user_id);
    }

    @GetMapping(path="/all")
    public @ResponseBody
    Iterable<Policy> getAllUsers() {
        // This returns a JSON or XML with the users
        return policyRepository.findAll();
    }

    @GetMapping("/policy/{id}")
    public Optional<Policy> getPolicybyId(@PathVariable String id){
        int policyId = Integer.parseInt(id);
        logger.info("Get policy for policy_id: " + id);
        return policyRepository.findById(policyId);
    }


}
