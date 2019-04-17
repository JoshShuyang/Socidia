package socialmediaprotection.project.dataRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialmediaprotection.project.entities.Author;

@Repository
public interface AuthorRepository  extends JpaRepository<Author, Integer> {
}
