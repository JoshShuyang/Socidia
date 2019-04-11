package socialmediaprotection.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import socialmediaprotection.project.dataRepository.AuthorRepository;
import socialmediaprotection.project.entities.Author;
import java.util.Optional;

@RestController
public class AuthorController {
    final static Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    AuthorRepository authorRepository;

    @GetMapping("/author/{id}")
    public Optional<Author> getAuhtorbyId(@PathVariable String id){
        int authorId = Integer.parseInt(id);
        logger.info("Get author for author id: " + id);
        return authorRepository.findById(authorId);
    }
}