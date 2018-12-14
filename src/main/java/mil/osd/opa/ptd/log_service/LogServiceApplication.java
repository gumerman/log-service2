package mil.osd.opa.ptd.log_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCrypt;

@SpringBootApplication
@EnableScheduling  // add if you have scheduled tasks(methods)
public class LogServiceApplication { // extends SpringBootServletInitializer {

  // Add this only if you have .JSP files
//  @Override
//  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//    return application.sources(LogServiceApplication.class);
//  }

  // Start website with:
  // http://localhost:8080/log-service
	public static void main(String[] args) {
	
		SpringApplication.run(LogServiceApplication.class, args);
	}
}
