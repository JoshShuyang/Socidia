package socidia.middleware_server.event;

import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import socidia.middleware_server.model.User;
import socidia.middleware_server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationSuccessEvent> {
    @Autowired
    private IUserService service;

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Autowired
    private TemplateEngine templateEngine;


    @Override
    public void onApplicationEvent(final  OnRegistrationSuccessEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationSuccessEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user, token);

        final MimeMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }

    private final MimeMessage constructEmailMessage(final OnRegistrationSuccessEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        final String message = messages.getMessage("message.regSucc", null, event.getLocale());
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getUsername());
        variables.put("message", message);
        variables.put("confirmationUrl", confirmationUrl);

        final String subject = "Socidia Registration Confirmation";
        final MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email);
        try {
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(generateMailHtml(variables,event), true);
            helper.setFrom(env.getProperty("support.email"));
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        return email;
    }

    public String generateMailHtml(Map<String, Object> variables, final OnRegistrationSuccessEvent event) {
        final String templateFileName = "email"; //Name of the template file without extension
        String output = this.templateEngine.process(templateFileName, new Context(event.getLocale(), variables));

        return output;
    }
}
