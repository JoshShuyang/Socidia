package socidia.middleware_server.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="role")
public class Role {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.ALL})
    private Set<User> users;

    private String name;

    public Role() {
    }

    public Role(String role_name) {
        this.name = role_name;
    }

    public Long getId() {
        return id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getRole_name() {
        return name;
    }

    public void setRole_name(String role_name) {
        this.name = role_name;
    }
}
