package socialmediaprotection.project.Scanner.Facebook;

import com.restfb.json.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FBPost {

    private static String ID = "id";
    private static String MESSAGE = "message";
    private static String FROM = "from";
    private static String NAME = "name";
    private static String CREATED_TIME = "created_time";

    private String postId;
    private String message;
    private List<FBComment> fbCommentList;
    private FBAuthor author;
    private Date date;

    public FBPost(JsonObject postObject) {
        this.postId = postObject.get(ID).asString().toString();
        if (postObject.get(MESSAGE) != null)
            this.message = postObject.get(MESSAGE).asString().toString();
        JsonObject authorInfo = postObject.get(FROM).asObject();
        this.author = new FBAuthor(authorInfo.get(NAME).asString().toString(), authorInfo.get(ID).asString().toString());
        fbCommentList = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        date = null;
        try {
            date = inputFormat.parse(postObject.get(CREATED_TIME).asString().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FBComment> getFbCommentList() {
        return fbCommentList;
    }

    public void setFbCommentList(List<FBComment> fbCommentList) {
        this.fbCommentList = fbCommentList;
    }

    public FBAuthor getAuthor() {
        return author;
    }

    public void setAuthor(FBAuthor author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
