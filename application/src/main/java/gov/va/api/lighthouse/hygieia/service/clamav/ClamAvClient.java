package gov.va.api.lighthouse.hygieia.service.clamav;

import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.DataTransferAborted;
import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.FailedToConnectToClamAvServer;
import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.FailedToReadDataFromClamAvServer;
import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.FailedToSendDataToClamAvServer;
import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.ScanFailed;
import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.VirusFound;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class ClamAvClient {
  @Getter private final ClamAvOptions options;

  private Connection createConnection() {
    try {
      return new Connection(options());
    } catch (IOException e) {
      throw new FailedToConnectToClamAvServer(e, options());
    }
  }

  /** Submit the the provided data for virus scanning. */
  public void scan(InputStream data) {
    try (var connection = createConnection()) {
      var reply = InstreamCommand.create(data).execute(connection);
      log.info(reply);
    }
  }

  interface Command<T> {
    T execute(Connection connection);
  }

  @RequiredArgsConstructor(staticName = "create")
  static class InstreamCommand implements Command<String> {
    private final InputStream data;

    @Override
    public String execute(Connection connection) {
      connection.send("zINSTREAM\0");
      connection.send(data, new byte[] {0, 0, 0, 0});
      var reply = connection.readReply();
      throwIfVirusFound(reply);
      throwIfScanFailed(reply);
      return reply;
    }

    private void throwIfScanFailed(String reply) {
      // stream: OK
      // INSTREAM size limit exceeded. ERROR
      if (reply.contains("ERROR")) {
        /*
         * The result of virus scan errors isn't well documented by ClamAV and I do not trust it not
         * to change slightly. For example, 'size limit exceeded' output has changed. To safely
         * identify an error, we'll take ERROR anywhere it appears and attempt to strip away the
         * leading command string and trailing ERROR marker to get the reason. If we the format
         * changes, we'll still be identifying a error was detected, but the reason name may not be
         * perfectly clean... assuming ERROR continues to be the marker.
         */
        var reason = reply.replaceAll("INSTREAM ", "").replace(" ERROR", "");
        throw ScanFailed.builder().clamServerReply(reply).reason(reason).build();
      }
    }

    private void throwIfVirusFound(String reply) {
      // stream: virus-name FOUND
      if (reply.contains("FOUND")) {
        /*
         * The result of virus scan isn't well documented by ClamAV and I do not trust it not to
         * change slightly. For example, 'size limit exceeded' output has changed. To safely
         * identify a virus, we'll take FOUND anywhere it appears and attempt to strip away the
         * leading stream identifier and trailing FOUND marker to get the virus name. If we the
         * format changes, we'll still be identifying a virus was detected, but the virus name may
         * not be perfectly clean... assuming FOUND continues to be the marker.
         */
        var virus = reply.replaceAll("^\\w+: ", "").replace(" FOUND", "");
        throw VirusFound.builder().clamServerReply(reply).virusName(virus).build();
      }
    }
  }

  private static class Connection implements AutoCloseable {

    private final ClamAvOptions options;

    private final Socket socket;

    private final BufferedOutputStream toServer;

    private final InputStream fromServer;

    Connection(ClamAvOptions options) throws UnknownHostException, IOException {
      this.options = options;
      this.socket = new Socket(options.host(), options.port());
      this.toServer = new BufferedOutputStream(socket.getOutputStream());
      this.fromServer = socket.getInputStream();
      socket.setSoTimeout((int) options.timeout().toMillis());
    }

    @Override
    public void close() {
      closeQuietly(socket);
      closeQuietly(toServer);
      closeQuietly(fromServer);
    }

    private void closeQuietly(Closeable closeMe) {
      if (closeMe == null) {
        return;
      }
      try {
        closeMe.close();
      } catch (IOException e) {
        /* ignore */
      }
    }

    public String readReply() {
      try {
        return StreamUtils.copyToString(fromServer, StandardCharsets.UTF_8).trim();
      } catch (IOException e) {
        throw new FailedToReadDataFromClamAvServer(e, options);
      }
    }

    String readReplyIfAvailable() {
      try {
        return fromServer.available() > 0 ? readReply() : null;
      } catch (IOException e) {
        throw new FailedToReadDataFromClamAvServer(e, options);
      }
    }

    void send(String text) {
      try {
        toServer.write(text.getBytes(StandardCharsets.UTF_8));
        toServer.flush();
      } catch (IOException e) {
        throw new FailedToSendDataToClamAvServer(e, options);
      }
    }

    /**
     * Send data to ClamAV server in chunks no larger that Options specified. Individual chunks may
     * be smaller. Chunk format is (chunk-size)(chunk-data) where chunk-size is a 4 byte unsigned
     * integer indicating the number of bytes of data being sent. A terminator character is sent
     * after all chunks have been transmitted to indicate end of data. The terminator depends on the
     * command, e.g., zINSTREAM requires a null character, while nINSTREAM requires a new line.
     *
     * <p>Transfer may be terminated by server, which is checked between sending chunks.
     */
    void send(InputStream data, byte[] terminator) {
      var chunk = new byte[options.chunkSizeInBytes()];
      var chunkSize = ByteBuffer.allocate(4);
      String reply = null;
      try {
        int dataBytesRead = data.read(chunk);
        while (dataBytesRead >= 0) {
          toServer.write(chunkSize.putInt(0, dataBytesRead).array());
          toServer.write(chunk, 0, dataBytesRead);
          reply = readReplyIfAvailable();
          if (reply != null) {
            throw new DataTransferAborted(reply);
          }
          dataBytesRead = data.read(chunk);
        }
        toServer.write(terminator);
        toServer.flush();
      } catch (IOException e) {
        throw new FailedToSendDataToClamAvServer(e, options);
      }
    }
  }
}
