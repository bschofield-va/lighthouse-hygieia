package gov.va.api.lighthouse.hygieia.service.antivirus;

import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface VirusScanner {
  void scan(InputStream data);

  @Value
  @RequiredArgsConstructor(staticName = "of")
  class ScanId {
    String value;
  }

  class VirusScanningException extends RuntimeException {
    public VirusScanningException(String message, Throwable cause) {
      super(message, cause);
    }

    public VirusScanningException(String message) {
      super(message);
    }
  }

  class ScanFailed extends VirusScanningException {

    public ScanFailed(String message, Throwable cause) {
      super(message, cause);
    }

    public ScanFailed(String message) {
      super(message);
    }
  }

  class VirusFound extends VirusScanningException {
    public VirusFound(String message) {
      super(message);
    }
  }
}
