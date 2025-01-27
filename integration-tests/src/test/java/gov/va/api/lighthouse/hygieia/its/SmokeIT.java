package gov.va.api.lighthouse.hygieia.its;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(Integer.MAX_VALUE)
public class SmokeIT extends EndpointValidation {

  @Test
  void scan() {
    new ScannerV1IT().scanClean();
  }
}
