package gov.va.api.lighthouse.hygieia.service.clamav;

import lombok.Getter;

public class ClamAvExceptions {
  public static class ClamAvException extends RuntimeException {
    private final ClamAvOptions options;

    ClamAvException(String message, ClamAvOptions options) {
      super(message);
      this.options = options;
    }

    ClamAvException(String message, Throwable cause, ClamAvOptions options) {
      super(message, cause);
      this.options = options;
    }
  }

  public static class FailedToConnectToClamAvServer extends ClamAvException {
    public FailedToConnectToClamAvServer(Throwable cause, ClamAvOptions options) {
      super("Failed to connect to virus scanner", cause, options);
    }
  }

  public static class FailedToSendDataToClamAvServer extends ClamAvException {
    public FailedToSendDataToClamAvServer(Throwable cause, ClamAvOptions options) {
      super("Failed to send data to virus scanner", cause, options);
    }
  }

  public static class FailedToReadDataFromClamAvServer extends ClamAvException {
    public FailedToReadDataFromClamAvServer(Throwable cause, ClamAvOptions options) {
      super("Failed to read results from virus scanner", cause, options);
    }
  }

  public static class DataTransferAborted extends ClamAvException {
    @Getter private String clamServerReply;

    public DataTransferAborted(ClamAvOptions options, String clamServerReply) {
      super("Data transfer aborted by virus scanner", options);
      this.clamServerReply = clamServerReply;
    }
  }
}
