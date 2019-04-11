package socialmediaprotection.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import socialmediaprotection.project.dataRepository.ItemRepository;
import socialmediaprotection.project.entities.Item;

import java.util.List;
import java.util.Optional;

@RestController
public class ItemController {
    final static Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/{userId}/item") //violated items of user 29
    public List<Item> getItemsbyUserId(@PathVariable String userId){
        int user_id = Integer.parseInt(userId);
        logger.info("Get item for user_id: " + userId);
        return itemRepository.findByUser_id(user_id);
    }

    @GetMapping("/item/{id}")
    public Optional<Item> getItembyId(@PathVariable String id){
        int policyId = Integer.parseInt(id);
        logger.info("Get item for item_id: " + id);
        return itemRepository.findById(policyId);
    }
}
