package socialmediaprotection.project.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import socialmediaprotection.project.Scanner.Facebook.FBPost;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonValue;
import socialmediaprotection.project.scheduler.ScheduledScan;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class FBScanner {

    private static final Logger log = LoggerFactory.getLogger(ScheduledScan.class);

    private static String FEED = "me/feed";
    private static String FIELDS = "fields";
    private static String DATA = "data";

    private FacebookClient facebookClient;
    private String options;
    private int userId;
    private List<FBPost> fbPosts;
    private String dataSource;
    private String username;
    private String password;
    private Date lastScanDate;
    private List<FBPost> unScannedFBPosts;
    private Map<Integer, String> policyRuleMapping;
    private Map<Integer, FBPost> violations;

    public FBScanner(String access_token, String options, int userId, String dataSource, String username, String password) {
        facebookClient = new DefaultFacebookClient(access_token, Version.VERSION_2_11);
        this.options = options;
        this.userId = userId;
        fbPosts = new LinkedList<>();
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
        unScannedFBPosts = new ArrayList<>();
        policyRuleMapping = new HashMap<>();
        violations = new HashMap<>();
    }

    public void scan() throws Exception {
        preScan();
        getPosts();
        if (unScannedFBPosts.size() == 0) {
            log.info("No new content!");
        } else {
            applyRulesToPost();
            dataPersistent();
        }
    }

    private void dataPersistent() throws SQLException {
        log.info("Data Persistent!");
        Connection con = DriverManager.getConnection(dataSource, username, password);;
        Statement stmt = null;
        int authorID = -1;
        int itemID = -1;

        for (Map.Entry<Integer, FBPost> entry: violations.entrySet()) {
            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from authors WHERE author_id = '" + entry.getValue().getAuthor().getAuthorId() + "' AND author_name = '" + entry.getValue().getAuthor().getName() + "'");
                if (!rs.next()) {
                    PreparedStatement preparedStmtForAuthor = con.prepareStatement("INSERT INTO authors (author_id, author_name) VALUES ('" + entry.getValue().getAuthor().getAuthorId() + "', '" + entry.getValue().getAuthor().getName() + "')");
                    preparedStmtForAuthor.execute();
                }
                ResultSet resultSet = stmt.executeQuery("select * from authors WHERE author_id = '" + entry.getValue().getAuthor().getAuthorId() + "' AND author_name = '" + entry.getValue().getAuthor().getName() + "'");
                while (resultSet.next()) {
                    authorID = resultSet.getInt(1);
                }

                PreparedStatement preparedStmt = con.prepareStatement("INSERT INTO items (user_id, author_id, post_content, page_name, item_type) VALUES (" + userId + ", " + authorID + ", '" + entry.getValue().getMessage() + "'," + "'Messi', '" + "FB" +"' )", Statement.RETURN_GENERATED_KEYS);
                preparedStmt.execute();

                try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itemID = generatedKeys.getInt(1);
                        PreparedStatement itemRuleStatement = con.prepareStatement("INSERT INTO item_violate_rules (item_id, rule_id) VALUES (" + itemID + "," + entry.getKey() + ")");
                        itemRuleStatement.execute();
                    }  else {
                        throw new SQLException("Creating item failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        con.close();
    }

    private void applyRulesToPost() {
        log.info("Apply rules to post!");
        for (FBPost fbPost: unScannedFBPosts) {
            for (Map.Entry<Integer, String> entry: policyRuleMapping.entrySet()) {
                String fbMessage = fbPost.getMessage();
                if (fbMessage != null && fbMessage.equals(entry.getValue())) {
                    violations.put(entry.getKey(), fbPost);
                }
            }
        }
    }

    private void preScan() throws SQLException {
        Connection con = null;
        Statement stmt = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            con = DriverManager.getConnection(dataSource, username, password);
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from items WHERE items.user_id = " + userId + " ORDER BY created_at DESC");
            while(rs.next()) {
                String dateStr = rs.getString(6).toString();

                try {
                    lastScanDate = inputFormat.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;
            }

            List<Integer> policyIds = new ArrayList<>();
            rs = stmt.executeQuery("select * from policy WHERE policy.user_id = " + userId);


            while(rs.next()) {
                policyIds.add(rs.getInt(1));
            }

            for (int policyId: policyIds) {
                rs = stmt.executeQuery("select * from policy_rules WHERE policy_rules.policy_id = " + policyId);

                while(rs.next()) {
                    int id = rs.getInt(1);
                    String content = rs.getString(4);
                    policyRuleMapping.put(id, content);
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void getPosts() {
        JsonObject jsonObject = facebookClient.fetchObject(FEED, JsonObject.class, Parameter.with(FIELDS, options));
        JsonArray listOfJsonObjects = (JsonArray)jsonObject.get(DATA);

        for (JsonValue postObject: listOfJsonObjects) {
            FBPost fbPost = new FBPost(postObject.asObject());
            fbPosts.add(fbPost);
        }

        excluedeScannedPost();

        log.info("The total unscanned post is {}", this.unScannedFBPosts.size());
    }

    private void excluedeScannedPost() {
        log.info("lastScanDate: {}", lastScanDate);
        log.debug("Last Scanned Item for userID: " + userId + " with Date: " + lastScanDate);
        for (FBPost fbPost: fbPosts) {
            if (!fbPost.getDate().before(lastScanDate)) {
                unScannedFBPosts.add(fbPost);
            }
        }
    }
}
