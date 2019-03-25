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
    private Date created_at;
    private Date updated_at;


    public Policy() {
    }

    public Policy(int userId) {

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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Policy {" +
                "policy_id=" + id +
                ", user_id='" + user_id + '\'' +
                ", policy_name='" + policy_name + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
