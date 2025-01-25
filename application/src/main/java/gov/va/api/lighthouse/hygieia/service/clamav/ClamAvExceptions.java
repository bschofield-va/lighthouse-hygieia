package gov.va.api.lighthouse.hygieia.service.clamav;

import lombok.Builder;
import lombok.Getter;

public class ClamAvExceptions {

  public static class ClamAvException extends RuntimeException {
    ClamAvException(String message, Throwable cause) {
      super(message, cause);
    }

    ClamAvException(String message) {
      super(message);
    }
  }

  public static class ClamAvCommunicationException extends ClamAvException {
    @Getter private final ClamAvOptions options;

    ClamAvCommunicationException(String message, ClamAvOptions options) {
      super(message);
      this.options = options;
    }

    ClamAvCommunicationException(String message, Throwable cause, ClamAvOptions options) {
      super(message, cause);
      this.options = options;
    }
  }

  public static class FailedToConnectToClamAvServer extends ClamAvCommunicationException {
    public FailedToConnectToClamAvServer(Throwable cause, ClamAvOptions options) {
      super("Failed to connect to virus scanner", cause, options);
    }
  }

  public static class FailedToSendDataToClamAvServer extends ClamAvCommunicationException {
    public FailedToSendDataToClamAvServer(Throwable cause, ClamAvOptions options) {
      super("Failed to send data to virus scanner", cause, options);
    }
  }

  public static class FailedToReadDataFromClamAvServer extends ClamAvCommunicationException {
    public FailedToReadDataFromClamAvServer(Throwable cause, ClamAvOptions options) {
      super("Failed to read reply from virus scanner", cause, options);
    }
  }

  public static class ClamAvReplyException extends ClamAvException {
    @Getter private final String reply;

    public ClamAvReplyException(String message, String clamServerReply) {
      super(message);
      this.reply = clamServerReply;
    }
  }

  public static class DataTransferAborted extends ClamAvReplyException {
    public DataTransferAborted(String clamServerReply) {
      super("Data transfer aborted by virus scanner: " + clamServerReply, clamServerReply);
    }
  }

  public static class VirusFound extends ClamAvReplyException {
    @Getter private final String virusName;

    @Builder
    public VirusFound(String clamServerReply, String virusName) {
      super("Virus detected: " + virusName, clamServerReply);
      this.virusName = virusName;
    }
  }

  public static class ScanFailed extends ClamAvReplyException {
    @Getter private final String reason;

    @Builder
    public ScanFailed(String clamServerReply, String reason) {
      super("Scan failed: " + reason, clamServerReply);
      this.reason = reason;
    }
  }
}
