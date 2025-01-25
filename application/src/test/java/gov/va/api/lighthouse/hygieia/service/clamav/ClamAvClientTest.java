package gov.va.api.lighthouse.hygieia.service.clamav;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gov.va.api.health.sentinel.configurablevalues.ConfigurableValues;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.ScanFailed;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.VirusFound;
import gov.va.api.lighthouse.hygieia.service.test.MockClamAv;
import gov.va.api.lighthouse.hygieia.service.test.MockClamAv.Interaction;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import lombok.SneakyThrows;
import org.apache.hc.core5.concurrent.CompletedFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClamAvClientTest {
  int port;

  String hostname;

  MockClamAv mockClamAv;

  Future<?> clamAvDoes(Interaction interaction) {
    if (mockClamAv == null) {
      return new CompletedFuture<>(null);
    }
    return mockClamAv.interact(interaction);
  }

  @BeforeEach
  @SneakyThrows
  void initializeMockServer() {
    boolean useMock =
        ConfigurableValues.get()
            .forPropertyName("clamav-client-test.mock-server-enabled")
            .orElse("true")
            .asBoolean();
    port =
        ConfigurableValues.get()
            .forPropertyName("clamav-client-test.clamav-server-port")
            .orElse("3310")
            .asInteger();
    hostname =
        ConfigurableValues.get()
            .forPropertyName("clamav-client-test.clamav-server-hostname")
            .orElse("localhost")
            .asString();
    if (useMock) {
      mockClamAv = new MockClamAv(0);
      hostname = "localhost";
      port = mockClamAv.port();
    }
  }

  private void scan(String payload) {
    var options = ClamAvProperties.builder().hostname(hostname).port(port).build();
    var clamAv = ClamAvClient.create(options);
    var in = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
    clamAv.scan(in);
  }

  @Test
  @SneakyThrows
  void scanAbortedTooLarge() {
    var interaction =
        clamAvDoes(
            session -> {
              session.readNullTerminatedLine();
              session.writeNullTerminatedLine("INSTREAM size limit exceeded. ERROR");
              session.readAllChunks();
            });
    assertThatExceptionOfType(ScanFailed.class)
        .isThrownBy(() -> scan("this is a clean file".repeat(2000)));
    interaction.get();
  }

  @Test
  @SneakyThrows
  void scanClean() {
    var payload = "this is a clean file".repeat(10);
    var interaction =
        clamAvDoes(
            session -> {
              var command = session.readNullTerminatedLine();
              var data = session.readAllChunks();
              session.writeNullTerminatedLine("stream: OK");
              assertThat(command).isEqualTo("zINSTREAM");
              assertThat(data).isEqualTo(payload);
            });
    scan(payload);
    interaction.get();
  }

  @Test
  @SneakyThrows
  void scanVirusDetected() {
    var payload = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";
    var interaction =
        clamAvDoes(
            session -> {
              session.readNullTerminatedLine();
              session.readAllChunks();
              session.writeNullTerminatedLine("stream: fake-test-virus FOUND");
            });
    assertThatExceptionOfType(VirusFound.class).isThrownBy(() -> scan(payload));
    interaction.get();
  }

  @AfterEach
  void shutdownMockServer() {
    if (mockClamAv != null) {
      mockClamAv.close();
    }
  }
}
