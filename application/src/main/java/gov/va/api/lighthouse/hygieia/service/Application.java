package gov.va.api.lighthouse.hygieia.service;

import gov.va.api.lighthouse.hygieia.service.config.OpenApiProperties;
import gov.va.api.lighthouse.hygieia.service.config.encryption.EncryptionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("WeakerAccess")
@SpringBootApplication(
    exclude = {
      SecurityAutoConfiguration.class,
      ManagementWebSecurityAutoConfiguration.class,
      UserDetailsServiceAutoConfiguration.class,
      ErrorMvcAutoConfiguration.class
    })
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties({EncryptionProperties.class, OpenApiProperties.class})
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
