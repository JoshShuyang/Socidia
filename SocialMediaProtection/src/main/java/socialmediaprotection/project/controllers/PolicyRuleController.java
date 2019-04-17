package socialmediaprotection.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import socialmediaprotection.project.dataRepository.PolicyRulesRepository;
import socialmediaprotection.project.entities.Policy;
import socialmediaprotection.project.entities.Rule;

import java.util.List;
import java.util.Optional;

@RestController
public class PolicyRuleController {
    final static Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    PolicyRulesRepository policyRulesRepository;

    @GetMapping("/policy/{policy_id}/rules")
    public List<Rule> getPolicybyUserId(@PathVariable String policy_id){
        int policyId = Integer.parseInt(policy_id);
        logger.info("Get policy rules for policy_id: " + policyId);
        return policyRulesRepository.findByPolicy_id(policyId);
    }

    @PostMapping("/policy_rule")
    Rule createNewPolicy(@RequestBody Rule rule) {
        return policyRulesRepository.save(rule);
    }

    @GetMapping("/policy_rule/{id}") //get rule info by rule id
    public Optional<Rule> getPolicybyId(@PathVariable String id){
        int ruleId = Integer.parseInt(id);
        logger.info("Get policy_rule for id: " + id);
        return policyRulesRepository.findById(ruleId);
    }

    @PutMapping("/policy_rule/{id}")
    Rule replacePolicyRule(@RequestBody Rule newPolicyRule, @PathVariable Integer id) {
        return policyRulesRepository.findById(id)
                .map(policyRule -> {
                    policyRule.setPolicy_id(newPolicyRule.getPolicy_id());
                    policyRule.setRule_name(newPolicyRule.getRule_name());
                    policyRule.setRule_content(newPolicyRule.getRule_content());
                    return policyRulesRepository.save(policyRule);
                })
                .orElseGet(() -> {
                    newPolicyRule.setId(id);
                    return policyRulesRepository.save(newPolicyRule);
                });
    }

    @DeleteMapping("/policy_rule/{id}")
    void deletePolicyRule(@PathVariable Integer id) {
        policyRulesRepository.deleteById(id);
        logger.info("Delete policy_rule for id: " + id);
    }

}
