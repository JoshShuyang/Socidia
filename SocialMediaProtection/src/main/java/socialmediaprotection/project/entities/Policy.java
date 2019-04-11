package socialmediaprotection.project.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(name = "Policy.findByUser_id", query = "SELECT p FROM Policy p WHERE LOWER(p.user_id) = LOWER(?1)")
@Table(name = "policy")
public class Policy {
    @Id
    @Column(name = "policy_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int user_id;
    private String policy_name;
    private int notification_type;

    public Policy() {
    }

    public Policy(int userId) {
        this.user_id = userId;
    }

    public Policy(int userId, String policy_name) {
        this.user_id = userId;
        this.policy_name = policy_name;
        this.notification_type = 0;
    }

    public Policy(int userId, String policy_name, int notification_type) {
        this.user_id = userId;
        this.policy_name = policy_name;
        this.notification_type = notification_type;
    }

    public int getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(int notification_type) {
        this.notification_type = notification_type;
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

    public String getPolicy_name() {
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {
        this.policy_name = policy_name;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", policy_name='" + policy_name + '\'' +
                ", notification_type=" + notification_type +
                '}';
    }
}
