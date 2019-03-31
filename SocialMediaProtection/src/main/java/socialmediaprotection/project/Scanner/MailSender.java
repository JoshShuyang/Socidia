package socialmediaprotection.project.Scanner;

/**
 * Created by Vencci on 2019/3/30.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
@Component("javasampleapproachMailSender")
public class MailSender {

        @Autowired
        JavaMailSender javaMailSender;

        Logger logger = LoggerFactory.getLogger(this.getClass());

        public void sendMail(String from, String to, String subject, String body) {
            logger.info("Sending...");
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                    messageHelper.setFrom("vickywenqiwang@gmail.com");
                    messageHelper.setTo("zhaochenqi2013@gmail.com");
                    messageHelper.setSubject("test_subjust");
                    messageHelper.setText("test_body");
                };
                try {
                    javaMailSender.send(messagePreparator);//send mail to SMTP server
                } catch (MailException e) {
                    // runtime exception; compiler will not force you to handle it
                }
            logger.info("Done!");
        }
    }

