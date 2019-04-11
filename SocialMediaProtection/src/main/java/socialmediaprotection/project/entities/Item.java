package socialmediaprotection.project.entities;

import javax.persistence.*;

@Entity
@NamedQuery(name = "Item.findByUser_id", query = "SELECT r FROM Item r WHERE LOWER(r.user_id) = LOWER(?1)")
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int author_id;
    private int user_id;
    private String post_content;
    private String page_name;
    private String item_type;

    public Item() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", author_id=" + author_id +
                ", user_id=" + user_id +
                ", post_content='" + post_content + '\'' +
                ", page_name='" + page_name + '\'' +
                ", item_type='" + item_type + '\'' +
                '}';
    }
}
