package gov.va.api.lighthouse.hygieia.service.clamav;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@ConfigurationProperties("clamav")
@AllArgsConstructor
@Builder
public class ClamAvProperties {
  @NotBlank private final String hostname;

  @Min(0)
  private final int port;

  @Builder.Default private final Duration timeout = Duration.ofSeconds(10);

  @Min(1)
  @Max(1024 * 1024)
  @Builder.Default
  private final int chunkSizeInBytes = 2048;

  /** Return a single line, simple summary suitable for logs. */
  public String summary() {
    return String.format(
        "%s:%d (timeout %d ms / chunk %d bytes)",
        hostname, port, timeout.toMillis(), chunkSizeInBytes);
  }
}
