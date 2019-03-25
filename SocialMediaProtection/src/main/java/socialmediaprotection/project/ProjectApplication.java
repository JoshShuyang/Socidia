package socialmediaprotection.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import socialmediaprotection.project.dataRepository.PolicyRepository;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ProjectApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ProjectApplication.class, args);


//		Properties props = PropertiesLoaderUtils.loadAllProperties(Configuraton.CONFIGURATION_FILE);
//		PropertyPlaceholderConfigurer properties = new PropertyPlaceholderConfigurer();
//		properties.setProperties(props);

//		String access_token = props.getProperty("access_token");
//		String options = props.getProperty("options");
//
//
//		FBScanner fbScanner = new FBScanner(access_token, options);
//		fbScanner.scan();


	}

}
