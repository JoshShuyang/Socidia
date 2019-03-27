package socialmediaprotection.project.Scanner.Facebook;

public class FBAuthor {
    private String name;
    private String authorId;

    public FBAuthor(String name, String authorId) {
        this.name = name;
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
