package gov.va.api.lighthouse.hygieia.service.scanner;

import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.hygieia.api.v1.ScanResponseV1;
import gov.va.api.lighthouse.hygieia.api.v1.ScannerV1Api;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.VirusFound;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

  private final ScanIdGenerator scanIdGenerator;

  @Override
  @SneakyThrows
  public ScanResponseV1 scan(MultipartFile file) {
    var scanId = scanIdGenerator.get();
    log.info("Starting scan {}", scanId);
    try {
      scanner.scan(file.getInputStream());
    } catch (VirusFound e) {
      log.warn("Virus found for scan {}: {}", scanId, e.getMessage());
      return ScanResponseV1.builder()
          .data(
              ScanResponseV1.Data.builder()
                  .scanId(scanId)
                  .virusFound(true)
                  .virusName(e.getMessage())
                  .build())
          .build();
    }
    return ScanResponseV1.builder()
        .data(ScanResponseV1.Data.builder().scanId(scanId).virusFound(false).build())
        .build();
  }
}
