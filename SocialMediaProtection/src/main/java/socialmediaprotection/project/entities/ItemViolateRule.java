package socialmediaprotection.project.entities;

import javax.persistence.*;

@Entity
@NamedQuery(name = "ItemViolateRule.findByItem_id", query = "SELECT r FROM ItemViolateRule r WHERE LOWER(r.item_id) = LOWER(?1)")
@Table(name = "item_violate_rules")
public class ItemViolateRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int item_id;
    private int rule_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getRule_id() {
        return rule_id;
    }

    public void setRule_id(int rule_id) {
        this.rule_id = rule_id;
    }

    @Override
    public String toString() {
        return "ItemViolateRule{" +
                "id=" + id +
                ", item_id=" + item_id +
                ", rule_id=" + rule_id +
                '}';
    }
}
