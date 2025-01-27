package gov.va.api.lighthouse.hygieia.service.config.encryption;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.health.autoconfig.encryption.BasicEncryption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EncryptionServiceConfig {

  @Bean
  EncryptionService instance(EncryptionProperties properties) {
    if (properties == null || isBlank(properties.key()) || "unset".equals(properties.key())) {
      log.warn("Encryption service set to redact. Decryption will not be possible.");
      return new RedactedTextEncryptionService();
    }
    if ("disabled".equals(properties.key())) {
      log.warn("Encryption service has been disabled. Messages will be in clear text.");
      return new DisabledTextEncryptionService();
    }
    log.info("Encryption service has been enabled.");
    return new BasicEncryptionService(properties.key());
  }

  @AllArgsConstructor
  private static class BasicEncryptionService implements EncryptionService {
    private final String key;

    @Override
    public String encrypt(String string) {
      return BasicEncryption.forKey(key).encrypt(string);
    }
  }

  private static class DisabledTextEncryptionService implements EncryptionService {
    @Override
    public String encrypt(String string) {
      return string;
    }
  }

  private static class RedactedTextEncryptionService implements EncryptionService {
    @Override
    public String encrypt(String string) {
      return "*****";
    }
  }
}
