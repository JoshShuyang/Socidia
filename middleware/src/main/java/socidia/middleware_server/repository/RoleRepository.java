package socidia.middleware_server.repository;

import socidia.middleware_server.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String rolename);
}
