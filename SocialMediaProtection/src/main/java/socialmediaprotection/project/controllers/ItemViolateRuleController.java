package socialmediaprotection.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import socialmediaprotection.project.dataRepository.ItemViolateRuleRepository;
import socialmediaprotection.project.entities.ItemViolateRule;

import java.util.List;

@RestController
public class ItemViolateRuleController {
    final static Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    ItemViolateRuleRepository itemViolateRuleRepository;

    @GetMapping("/item_violate_rule/{itemId}") //for item 3 violate [rules]
    public List<ItemViolateRule> getItemsViolateRulesbyItemId(@PathVariable String itemId){
        int item_id = Integer.parseInt(itemId);
        logger.info("Get item violates rules for item_id: " + itemId);
        return itemViolateRuleRepository.findByItem_id(item_id);
    }
}
