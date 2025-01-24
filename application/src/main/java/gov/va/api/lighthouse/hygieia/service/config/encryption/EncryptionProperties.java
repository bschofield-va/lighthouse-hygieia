package gov.va.api.lighthouse.hygieia.service.config.encryption;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@ConfigurationProperties("encryption-service")
@AllArgsConstructor
@Builder
public class EncryptionProperties {
  private final String key;

  @AssertTrue
  boolean isEncryptionKeySet() {
    return isNotBlank(key) && !"unset".equals(key);
  }
}
