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
import java.util.Date;
import java.util.Properties;

public class MailSender {

    public void send(String receipient, String policyType, Date ts, String accountType) {
        // Recipient's email ID needs to be mentioned.
        String to = receipient;

        // Sender's email ID needs to be mentioned
        String from = "zhaochenqi2013@gmail.com";
        String password = "Dz_01290901";

        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";
        String port = "587";


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
            InternetAddress addressFrom = new InternetAddress(from);

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

            // send the HTML
            message.setContent("<h1>This is HTML message</h1>", "text/html");


            // Send message
            transport.connect();
            Transport.send(message);
            System.out.println("Sent message successfully....");
            transport.close();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
