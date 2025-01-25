package gov.va.api.lighthouse.hygieia.service.test;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockClamAv implements AutoCloseable {
  @Getter private final int port;

  private ServerSocket serverSocket;

  private ExecutorService executor;

  public MockClamAv(int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    this.port = serverSocket.getLocalPort();
    executor = Executors.newSingleThreadExecutor();
    log.info("Server running on {}", this.port);
  }

  static void closeQuietly(Closeable closeMe) {
    if (closeMe == null) {
      return;
    }
    try {
      closeMe.close();
    } catch (IOException e) {
      /* ignore */
    }
  }

  @Override
  public void close() {
    executor.shutdownNow();
    closeQuietly(serverSocket);
  }

  public Future<?> interact(Interaction interaction) {
    return executor.submit(new InteractionTask(interaction));
  }

  @FunctionalInterface
  public interface Interaction {
    void performInteraction(ClientSession session);
  }

  public static class ClientSession implements AutoCloseable {
    private static byte[] NULL_CHAR = new byte[] {0, 0, 0, 0};

    private final Socket clientSocket;

    @Getter private final BufferedInputStream fromClient;

    @Getter private final BufferedOutputStream toClient;

    public ClientSession(Socket clientSocket) throws IOException {
      this.clientSocket = clientSocket;
      this.fromClient = new BufferedInputStream(clientSocket.getInputStream());
      this.toClient = new BufferedOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void close() {
      closeQuietly(clientSocket);
      closeQuietly(fromClient);
      closeQuietly(toClient);
    }

    @SneakyThrows
    public String readAllChunks() {
      var allChunks = new StringBuilder();
      var chunk = readChunk();
      while (isNotEmpty(chunk)) {
        allChunks.append(chunk);
        chunk = readChunk();
      }
      return allChunks.toString();
    }

    @SneakyThrows
    public String readChunk() {
      var size = readNextByteOrDie();
      var data = new byte[size];
      if (fromClient().read(data) < size) {
        throw new EOFException();
      }
      return new String(data, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public int readNextByteOrDie() {
      var b = fromClient().read();
      if (b == -1) {
        throw new EOFException();
      }
      return b;
    }

    @SneakyThrows
    public String readNullTerminatedLine() {
      var line = new StringBuilder();
      var available = fromClient().available();
      int nullCounter = 4;
      while (true) {
        int ch = readNextByteOrDie();
        line.append((char) ch);
        if (ch == 0) {
          nullCounter--;
          if (nullCounter == 0) {
            break;
          }
        }
      }
      var value = line.toString();
      return line.toString().trim();
    }

    @SneakyThrows
    public void writeNullTerminatedLine(String line) {
      toClient().write((line + "\0").getBytes(StandardCharsets.UTF_8));
      toClient().flush();
    }
  }

  @RequiredArgsConstructor
  class InteractionTask implements Runnable {
    final Interaction interaction;

    @SneakyThrows
    public void run() {
      try (var session = new ClientSession(serverSocket.accept())) {
        interaction.performInteraction(session);
      }
    }
  }
}
