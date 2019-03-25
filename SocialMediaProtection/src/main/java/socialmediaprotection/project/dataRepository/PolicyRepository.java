package socialmediaprotection.project.dataRepository;

import socialmediaprotection.project.entities.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
    List<Policy> findByUser_id(int user_id);
}
