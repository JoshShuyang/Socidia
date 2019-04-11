package socialmediaprotection.project.Scanner;

/**
 * Created by Vencci on 2019/4/1.
 */
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsSender {
    // Find your Account Sid and Auth Token at twilio.com/console
    public static final String ACCOUNT_SID =
            "ACe616f19e0e0f95d89f93b7d9a04f7487";
    public static final String AUTH_TOKEN =
            "db22743cd919fcdaf3a8e31ce3e83417";

    public void send(String targetPhoneNum, String msg) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new PhoneNumber(targetPhoneNum), // to
                        new PhoneNumber("+19843648567"), // from
                        msg)
                .create();

        System.out.println(message.getSid());
    }
}
