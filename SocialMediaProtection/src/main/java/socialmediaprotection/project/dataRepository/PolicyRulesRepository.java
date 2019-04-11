package socialmediaprotection.project.dataRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialmediaprotection.project.entities.Rule;

import java.util.List;

@Repository
public interface PolicyRulesRepository extends JpaRepository<Rule, Integer> {
    List<Rule> findByPolicy_id(int policy_id);
}

