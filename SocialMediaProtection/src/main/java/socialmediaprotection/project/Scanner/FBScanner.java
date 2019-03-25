package socialmediaprotection.project.Scanner;

import socialmediaprotection.project.Scanner.Facebook.FBPost;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class FBScanner {

    private static String FEED = "me/feed";
    private static String FIELDS = "fields";
    private static String DATA = "data";

    private FacebookClient facebookClient;
    private String options;
    private List<FBPost> fbPosts;

    public FBScanner(String access_token, String options) {
        facebookClient = new DefaultFacebookClient(access_token, Version.VERSION_2_11);
        this.options = options;
        fbPosts = new ArrayList<>();
    }

    public void scan() {
        preScan();
        getPosts();
    }

    private void preScan() {

    }

    private void getPosts() {
        JsonObject jsonObject = facebookClient.fetchObject(FEED, JsonObject.class, Parameter.with(FIELDS, options));
        JsonArray listOfJsonObjects = (JsonArray)jsonObject.get(DATA);

        for (JsonValue postObject: listOfJsonObjects) {
            FBPost fbPost = new FBPost(postObject.asObject());
            fbPosts.add(fbPost);
        }

        System.out.println(listOfJsonObjects);
    }
}
