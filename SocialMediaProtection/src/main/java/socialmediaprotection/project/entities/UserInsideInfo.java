package socialmediaprotection.project.entities;

import javax.persistence.*;

@Entity
@NamedQuery(name = "UserInsideInfo.findByUser_id", query = "SELECT r FROM UserInsideInfo r WHERE LOWER(r.user_id) = LOWER(?1)")
@Table(name = "user_inside_info")
public class UserInsideInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int user_id;
    private int number_policy_violation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getNumber_policy_violation() {
        return number_policy_violation;
    }

    public void setNumber_policy_violation(int number_policy_violation) {
        this.number_policy_violation = number_policy_violation;
    }

    @Override
    public String toString() {
        return "UserInsideInfo{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", number_policy_violation=" + number_policy_violation +
                '}';
    }
}
