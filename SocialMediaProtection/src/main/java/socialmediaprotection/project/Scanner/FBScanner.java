package socialmediaprotection.project.Scanner;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import socialmediaprotection.project.Scanner.Facebook.FBPost;
import socialmediaprotection.project.scheduler.ScheduledScan;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
    @Autowired
    @Qualifier("javasampleapproachMailSender")
    public MailSender mailSender;
    public SmsSender smsSender;
    public String targetNum;
    public int notifyType;
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
        mailSender = new socialmediaprotection.project.Scanner.MailSender();
        smsSender = new SmsSender();
    }

    public void scan() throws Exception {
        preScan();

        getPosts();
        if (unScannedFBPosts.size() == 0) {
            log.info("No new content!");
        } else {
            applyRulesToPost();
            dataPersistent();
            notifyType = getNotifyType();
            if (notifyType == 0) {
                sendMail();
            } else {
                targetNum = getTargetNum();
                sendSms(targetNum, "Socidia: A new security threat was detected for your social account: Facebook. Log in to your Socidia user portal to view details and take actions.");
            }
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

                PreparedStatement preparedStmt = con.prepareStatement("INSERT INTO items (user_id, author_id, post_content, page_name, item_type) VALUES (" + userId + ", " + authorID + ", '" + entry.getValue().getMessage() + "'," + "'Messi', '" + "Facebook" +"' )", Statement.RETURN_GENERATED_KEYS);
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
    public void sendMail() {
        String policyType = "policyType";
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String lastScanDateUTC = inputFormat.format(lastScanDate);

        System.out.println(lastScanDateUTC);

        try {
            String queryString = String.format("select policy_name\n" +
                    "from policy, policy_rules, item_violate_rules, items\n" +
                    "where items.user_id = %s and \n" +
                    "\t\titems.updated_at > '%s' and \n" +
                    "\t\titems.id = item_violate_rules.item_id and \n" +
                    "\t\titem_violate_rules.rule_id = policy_rules.id and \n" +
                    "\t\tpolicy_rules.policy_id = policy.policy_id;", userId, lastScanDateUTC);
            log.info("policyType query string is " + queryString);
            Connection conn = DriverManager.getConnection(dataSource, username, password);;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                sb.append(rs.getString(1)).append(", ");
            }

            sb.delete(sb.length() - 2, sb.length());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        policyType = sb.toString().equals("") ? "N/A" : sb.toString();


        String ruleType = "ruleType";
        sb = new StringBuilder();
        try {
            String queryString = String.format("select `rule_name` \n" +
                    "from `policy_rules`, `item_violate_rules`, `items` \n" +
                    "where  `items`.`user_id` = %s and \n" +
                    "\t\t`items`.`updated_at` > '%s' and \n" +
                    "\t\t`items`.`id` = `item_violate_rules`.`item_id` and \n" +
                    "\t\t`item_violate_rules`.`rule_id` = `policy_rules`.`id`;", userId, lastScanDateUTC);
            log.info("ruleType query string is " + queryString);
            Connection conn = DriverManager.getConnection(dataSource, username, password);;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                sb.append(rs.getString(1)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ruleType = sb.toString().equals("") ? "N/A" : sb.toString();

        String accountType = "accountType";
        sb = new StringBuilder();
        try {
            String queryString = String.format("select item_type\n" +
                    "from items\n" +
                    "where items.user_id = %s and " +
                    "items.updated_at > '%s';", userId, lastScanDateUTC);
            log.info("accountType query string is " + queryString);
            Connection conn = DriverManager.getConnection(dataSource, username, password);;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                sb.append(rs.getString(1)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        accountType = sb.toString().equals("") ? "N/A" : sb.toString();

        String recipient = "recipient";
        try {
            String queryString = String.format("select email from user where id = %s;", userId);
            log.info("recipient email query string is " + queryString);
            Connection conn = DriverManager.getConnection(dataSource, username, password);;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                recipient = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String ts = LocalDateTime.now().toString();
        log.info("sending alert to " + recipient + " policy type is " + policyType + ", rule type is " + ruleType + "account type is " + accountType + " time is " + ts);
        //change vickywenqiwang@gmail.com to recipient
        mailSender.send(recipient, policyType, ts, ruleType, accountType);
    }
    public void sendSms(String targetNumber, String msg) {
        smsSender.send(targetNumber, msg);
    }

    public int getNotifyType() {
        int notifyType = 0;
        try {
            String queryString = String.format("select notification_type from policy where user_id = %s;", userId);
            log.info("notification type query string is " + queryString);
            Connection conn = DriverManager.getConnection(dataSource, username, password);;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                notifyType = Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifyType;
    }

    public String getTargetNum() {
        String targetNum = "+19196995879";
        try {
            String queryString = String.format("select phone_num from user where id = %s;", userId);
            log.info("target number query string is " + queryString);
            Connection conn = DriverManager.getConnection(dataSource, username, password);;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                targetNum = rs.getString(1);
            }
            targetNum = "+1" + targetNum;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return targetNum;
    }
}
