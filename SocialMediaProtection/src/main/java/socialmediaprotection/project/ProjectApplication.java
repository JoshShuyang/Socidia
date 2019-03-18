package socialmediaprotection.project;

import config.Configuraton;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ProjectApplication.class, args);

		Properties props = PropertiesLoaderUtils.loadAllProperties(Configuraton.CONFIGURATION_FILE);
		PropertyPlaceholderConfigurer properties = new PropertyPlaceholderConfigurer();
		properties.setProperties(props);


		System.out.println(props.getProperty("access_token"));
	}

}
