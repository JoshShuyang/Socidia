package socialmediaprotection.project.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(name = "UserInsideInfo.findByUser_id", query = "SELECT r FROM UserInsideInfo r WHERE LOWER(r.user_id) = LOWER(?1)")
@Table(name = "user_inside_info")
public class UserInsideInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int user_id;
    private int number_policy_violation;
    private int total_polices;
    private int total_rules;
    private int number_rules_violation;
    private String created_at;


    public int getTotal_polices() {
        return total_polices;
    }

    public void setTotal_polices(int total_polices) {
        this.total_polices = total_polices;
    }

    public int getTotal_rules() {
        return total_rules;
    }

    public void setTotal_rules(int total_rules) {
        this.total_rules = total_rules;
    }

    public int getNumber_rules_violation() {
        return number_rules_violation;
    }

    public void setNumber_rules_violation(int number_rules_violation) {
        this.number_rules_violation = number_rules_violation;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

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
                ", total_polices=" + total_polices +
                ", total_rules=" + total_rules +
                ", number_rules_violation=" + number_rules_violation +
                ", created_at=" + created_at +
                '}';
    }
}
