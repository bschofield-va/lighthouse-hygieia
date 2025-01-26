package gov.va.api.lighthouse.hygieia.service.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import gov.va.api.lighthouse.hygieia.api.v1.test.V1Data;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.VirusFound;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ScannerV1ControllerTest {

  @Mock VirusScanner scanner;

  ScannerV1Controller controller() {
    return new ScannerV1Controller(scanner);
  }

  @Test
  void scanClean() {
    var mpf = mockMultipartFile();
    var response = controller().scan(mpf);
    assertThat(response).isEqualTo(V1Data.get().cleanScanResponse());
  }

  @Test
  void scanVirusFound() {
    var mpf = mockMultipartFile();
    doThrow(new VirusFound("fugazi")).when(scanner).scan(any(InputStream.class));
    var response = controller().scan(mpf);
    assertThat(response).isEqualTo(V1Data.get().virusFoundScanResponse("fugazi"));
  }

  private static MockMultipartFile mockMultipartFile() {
    return new MockMultipartFile("file", "howdy ho".getBytes(StandardCharsets.UTF_8));
  }
}
