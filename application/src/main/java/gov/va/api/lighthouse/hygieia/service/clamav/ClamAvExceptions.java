package gov.va.api.lighthouse.hygieia.service.clamav;

import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.ScanFailed;
import lombok.Getter;

public class ClamAvExceptions {

  public static class ClamAvCommunicationException extends ScanFailed {
    @Getter private final ClamAvProperties options;

    ClamAvCommunicationException(String message, Throwable cause, ClamAvProperties options) {
      super(message + " [" + options.summary() + "]", cause);
      this.options = options;
    }
  }

  public static class FailedToConnectToClamAvServer extends ClamAvCommunicationException {
    public FailedToConnectToClamAvServer(Throwable cause, ClamAvProperties options) {
      super("Failed to connect to virus scanner", cause, options);
    }
  }

  public static class FailedToSendDataToClamAvServer extends ClamAvCommunicationException {
    public FailedToSendDataToClamAvServer(Throwable cause, ClamAvProperties options) {
      super("Failed to send data to virus scanner", cause, options);
    }
  }

  public static class FailedToReadDataFromClamAvServer extends ClamAvCommunicationException {
    public FailedToReadDataFromClamAvServer(Throwable cause, ClamAvProperties options) {
      super("Failed to read reply from virus scanner", cause, options);
    }
  }

  public static class DataTransferAborted extends ScanFailed {
    public DataTransferAborted(String clamServerReply) {
      super("Data transfer aborted by virus scanner: " + clamServerReply);
    }
  }
}
