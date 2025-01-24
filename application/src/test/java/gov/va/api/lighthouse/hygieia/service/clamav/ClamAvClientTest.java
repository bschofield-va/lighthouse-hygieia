package gov.va.api.lighthouse.hygieia.service.clamav;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ClamAvClientTest {
  private void scan(String payload) {
    var options = ClamAvOptions.builder().host("localhost").port(3310).build();
    var clamAv = ClamAvClient.create(options);
    var in = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
    clamAv.scan(in);
  }

  @Test
  void scanClean() {
    scan("this is a clean file".repeat(2000));
  }

  @Test
  void scanVirusDetected() {
    scan("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*");
  }
}
