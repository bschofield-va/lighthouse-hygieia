package gov.va.api.lighthouse.hygieia.service.clamav;

import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ClamAvVirusScannerConfig {
  @Bean
  @RequestScope
  VirusScanner createClient(ClamAvProperties properties) {
    return ClamAvClient.create(properties);
  }
}
