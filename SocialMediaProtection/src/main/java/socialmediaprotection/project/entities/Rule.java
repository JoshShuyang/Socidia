package socialmediaprotection.project.entities;

import javax.persistence.*;

@Entity
@NamedQuery(name = "Rule.findByPolicy_id", query = "SELECT r FROM Rule r WHERE LOWER(r.policy_id) = LOWER(?1)")
@Table(name = "policy_rules")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int policy_id;
    private String rule_name;
    private String rule_content;

    public Rule() {

    }

    public Rule(int policy_id, String rule_name, String rule_content) {
        this.policy_id = policy_id;
        this.rule_name = rule_name;
        this.rule_content = rule_content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPolicy_id() {
        return policy_id;
    }

    public void setPolicy_id(int policy_id) {
        this.policy_id = policy_id;
    }

    public String getRule_name() {
        return rule_name;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public String getRule_content() {
        return rule_content;
    }

    public void setRule_content(String rule_content) {
        this.rule_content = rule_content;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", policy_id=" + policy_id +
                ", rule_name='" + rule_name + '\'' +
                ", rule_content='" + rule_content + '\'' +
                '}';
    }
}
