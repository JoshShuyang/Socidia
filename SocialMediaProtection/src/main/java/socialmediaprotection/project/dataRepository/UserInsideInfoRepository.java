package socialmediaprotection.project.dataRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialmediaprotection.project.entities.UserInsideInfo;

import java.util.List;

@Repository
public interface UserInsideInfoRepository extends JpaRepository<UserInsideInfo, Integer> {
    List<UserInsideInfo> findByUser_id(int user_id);
}
