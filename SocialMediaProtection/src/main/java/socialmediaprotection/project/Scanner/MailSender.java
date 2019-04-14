package socialmediaprotection.project.Scanner;

/**
 * Created by Vencci on 2019/3/30.
 */
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.mail.javamail.MimeMessagePreparator;
//import org.springframework.stereotype.Component;
//@Component("javasampleapproachMailSender")
//public class MailSender {
//
//    @Autowired
//    JavaMailSender javaMailSender;
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    public void sendMail(String from, String to, String subject, String body) {
//        logger.info("Sending...");
//            MimeMessagePreparator messagePreparator = mimeMessage -> {
//                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
//                messageHelper.setFrom("vickywenqiwang@gmail.com");
//                messageHelper.setTo("zhaochenqi2013@gmail.com");
//                messageHelper.setSubject("test_subjust");
//                messageHelper.setText("test_body");
//            };
//            try {
//                javaMailSender.send(messagePreparator);//send mail to SMTP server
//            } catch (MailException e) {
//                // runtime exception; compiler will not force you to handle it
//            }
//        logger.info("Done!");
//    }
//}


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class MailSender {

    public void send(String recipient, String policyType, String ts, String ruleType, String accountType) {
        // Recipient's email ID needs to be mentioned.
        String to = recipient;

        // Sender's email ID needs to be mentioned
        String from = "capstonesocidia@gmail.com";
        String password = "capstone2019";

        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";
        String port = "587";
        String facebook = "https://ibb.co/jJ7PDYv";
        String google = "https://ibb.co/p3MCP30";
        String twitter = "https://ibb.co/Vvh4MkL";
        String logo = "https://ibb.co/S0yYHhf";
//        imgMap.put()


        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,password);
                    }
                });

        try {
            Transport transport = session.getTransport();

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Socidia: New Security Threat Detected for Your Social Account");

            // Now set the actual message
            //message.setText("This is actual message");

            String html = readLineByLine("/Users/Vencci/Documents/SJSU Spring2019/CMPE 295B/Socidia/SocialMediaProtection/src/main/java/socialmediaprotection/project/Scanner/email.html");
            // send the HTML
           // message.setContent(String.format("<h1>This is HTML message. Policy Type is %s, Time: %s</h1>", policyType, ts.toString()), "text/html");
            String content = html.replaceFirst("nowis2019-04-01", ts);
            //content = content.replaceFirst("hereisaccounttype", accountType);
            content = content.replaceFirst("hereispolicytype", policyType);
            content = content.replaceFirst("hereisruletype", ruleType);
            message.setContent(content, "text/html");

            // Send message
            transport.connect();
            Transport.send(message);
            System.out.println("Sent message successfully....");
            transport.close();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static String readLineByLine(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
