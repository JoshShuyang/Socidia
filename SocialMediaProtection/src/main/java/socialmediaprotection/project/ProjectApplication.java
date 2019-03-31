package socialmediaprotection.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import socialmediaprotection.project.Scanner.FBScanner;
import socialmediaprotection.project.Scanner.MailSender;
import socialmediaprotection.project.config.Configuraton;

import java.util.Properties;

@SpringBootApplication
@EnableScheduling
public class ProjectApplication {
	@Autowired
	@Qualifier("javasampleapproachMailSender")
	public MailSender mailSender;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ProjectApplication.class, args);



		Properties props = PropertiesLoaderUtils.loadAllProperties(Configuraton.CONFIGURATION_FILE);
		PropertyPlaceholderConfigurer properties = new PropertyPlaceholderConfigurer();
		properties.setProperties(props);

		String access_token = props.getProperty("access_token");
		String options = props.getProperty("options");
		String d = props.getProperty("spring.datasource.url");
		String u = props.getProperty("spring.datasource.username");
		String p = props.getProperty("spring.datasource.password");



		FBScanner fbScanner = new FBScanner(access_token, options ,29, d, u, p);
		fbScanner.scan();




	}

	public void run(String... arg0) throws Exception {

		String from = "zhaochenqi2013@gmail.com";
		String to = "vickywenqiwang@gmail.com";
		String subject = "JavaMailSender";
		String body = "Just-Testing!";

		mailSender.sendMail(from, to, subject, body);
	}

}
