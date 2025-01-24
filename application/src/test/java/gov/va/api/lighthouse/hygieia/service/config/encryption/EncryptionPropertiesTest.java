package gov.va.api.lighthouse.hygieia.service.config.encryption;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EncryptionPropertiesTest {

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
      ,false
        ,false
      unset, false
      disabled,true
      whatever,true
      """)
  void isEncryptionKeySet(String key, boolean expected) {
    assertThat(new EncryptionProperties(key).isEncryptionKeySet()).isEqualTo(expected);
  }
}
