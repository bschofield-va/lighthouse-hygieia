package gov.va.api.lighthouse.hygieia.service.scanner;

import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.hygieia.api.v1.ScanResponseV1;
import gov.va.api.lighthouse.hygieia.api.v1.ScannerV1Api;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.ScanFailed;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.VirusFound;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
@Loggable
@Slf4j
public class ScannerV1Controller implements ScannerV1Api {

  private final VirusScanner scanner;

  @Override
  public ScanResponseV1 scan(MultipartFile file) {
    // var options = ClamAvOptions.builder().host("host.docker.internal").port(3310).build();
    // var clamAv = ClamAvClient.create(options);
    try {
      scanner.scan(file.getInputStream());
    } catch (VirusFound e) {
      return ScanResponseV1.builder()
          .data(ScanResponseV1.Data.builder().virusFound(true).virusName(e.getMessage()).build())
          .build();
    } catch (IOException e) {
      throw new ScanFailed("Network error while reading submitted file", e);
    }
    return ScanResponseV1.builder()
        .data(ScanResponseV1.Data.builder().virusFound(false).build())
        .build();
  }
}
