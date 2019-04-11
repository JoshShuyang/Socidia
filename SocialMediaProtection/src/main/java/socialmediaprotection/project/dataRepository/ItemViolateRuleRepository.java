package socialmediaprotection.project.dataRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialmediaprotection.project.entities.ItemViolateRule;

import java.util.List;

@Repository
public interface ItemViolateRuleRepository extends JpaRepository<ItemViolateRule, Integer> {
    List<ItemViolateRule> findByItem_id(int item_id);
}
