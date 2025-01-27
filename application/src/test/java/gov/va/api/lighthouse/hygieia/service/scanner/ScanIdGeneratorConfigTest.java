package gov.va.api.lighthouse.hygieia.service.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScanIdGeneratorConfigTest {

  @Test
  void generatesGeneratesNewId() {
    var ids = new ScanIdGeneratorConfig().scanIdGenerator();
    var id1 = ids.get();
    var id2 = ids.get();
    assertThat(id1).isNotBlank();
    assertThat(id2).isNotBlank();
    assertThat(id2).isNotEqualTo(id1);
  }
}
