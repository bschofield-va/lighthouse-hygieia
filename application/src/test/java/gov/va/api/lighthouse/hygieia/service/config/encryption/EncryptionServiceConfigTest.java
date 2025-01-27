package gov.va.api.lighthouse.hygieia.service.config.encryption;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.encryption.BasicEncryption;
import org.junit.jupiter.api.Test;

class EncryptionServiceConfigTest {

  EncryptionProperties _properties(String key) {
    return EncryptionProperties.builder().key(key).build();
  }

  @Test
  void encryptedImplementationAreBasedOnPropertyValue() {
    EncryptionServiceConfig config = new EncryptionServiceConfig();
    assertThat(config.instance(null).encrypt("shanktopus")).isEqualTo("*****");
    assertThat(config.instance(_properties("unset")).encrypt("shanktopus")).isEqualTo("*****");
    assertThat(config.instance(_properties("")).encrypt("shanktopus")).isEqualTo("*****");
    assertThat(config.instance(_properties("   ")).encrypt("shanktopus")).isEqualTo("*****");
    assertThat(config.instance(_properties("disabled")).encrypt("shanktopus"))
        .isEqualTo("shanktopus");

    BasicEncryption encryption = BasicEncryption.forKey("secret");
    String encrypted = config.instance(_properties("secret")).encrypt("shanktopus");
    assertThat(encrypted).isNotEqualTo("shanktopus");
    assertThat(encryption.decrypt(encrypted)).isEqualTo("shanktopus");
  }
}
