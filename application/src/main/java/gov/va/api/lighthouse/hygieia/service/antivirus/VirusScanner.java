package gov.va.api.lighthouse.hygieia.service.antivirus;

import java.io.InputStream;

public interface VirusScanner {
  void scan(InputStream data);

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
