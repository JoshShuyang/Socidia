package socialmediaprotection.project.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import socialmediaprotection.project.Scanner.FBScanner;
import javax.annotation.Resource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledScan {

    private static final Logger log = LoggerFactory.getLogger(ScheduledScan.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private Environment environment;

    @Scheduled(fixedRate = 300000) //change to 300000
    public void periodicScan() throws Exception {
        log.info("The time is now {}", dateFormat.format(new Date()));
        String access_token = getAccessToken();
        String options = environment.getProperty("options");
        String dataSource = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");

        List<Integer> ids = getUserIds();

        for (Integer id: ids) {
            if (id.intValue() != 29) continue;
            FBScanner fbScanner = new FBScanner(access_token, options, id.intValue(), dataSource, username, password);
            fbScanner.scan();
        }
    }

    public String getAccessToken() {
        String dataSource = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");

        Connection con= null;
        Statement stmt= null;
        String access_token = "";

        try {
            con = DriverManager.getConnection(dataSource, username, password);
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from user_token");

            while(rs.next())
                access_token = rs.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return access_token;
    }

    public List<Integer> getUserIds() {
        List<Integer> ids = new ArrayList<>();
        String dataSource = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");


        Connection con= null;
        Statement stmt= null;

        try {
            con = DriverManager.getConnection(dataSource, username, password);
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from user");

            while(rs.next())
                ids.add(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }
}
