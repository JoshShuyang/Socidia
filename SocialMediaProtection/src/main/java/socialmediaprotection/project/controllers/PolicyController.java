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

    @GetMapping("/{userId}/policy") // polcies of user 29 
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

    @PostMapping("/policy")
    Policy createNewPolicy(@RequestBody Policy policy) {
        return policyRepository.save(policy);
    }

    @PutMapping("/policy/{id}")
    Policy replacePolicy(@RequestBody Policy newPolicy, @PathVariable Integer id) {
        return policyRepository.findById(id)
                .map(policy -> {
                    policy.setUser_id(newPolicy.getUser_id());
                    policy.setPolicy_name(newPolicy.getPolicy_name());
                    policy.setNotification_type(newPolicy.getNotification_type());
                    return policyRepository.save(policy);
                })
                .orElseGet(() -> {
                    newPolicy.setId(id);
                    return policyRepository.save(newPolicy);
                });
    }

    @GetMapping("/policy/{id}")
    public Optional<Policy> getPolicybyId(@PathVariable String id){
        int policyId = Integer.parseInt(id);
        logger.info("Get policy for policy_id: " + id);
        return policyRepository.findById(policyId);
    }

    @DeleteMapping("/policy/{id}")
    void deletePolicy(@PathVariable Integer id) {
        policyRepository.deleteById(id);
        logger.info("Policy has been deleted for policy_id: " + id);
    }
}
