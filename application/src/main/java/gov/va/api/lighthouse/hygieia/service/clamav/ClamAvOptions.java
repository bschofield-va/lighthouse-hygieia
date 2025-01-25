package gov.va.api.lighthouse.hygieia.service.clamav;

import java.time.Duration;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClamAvOptions {
  String hostname;
  int port;
  @Builder.Default Duration timeout = Duration.ofSeconds(10);

  @Builder.Default int chunkSizeInBytes = 2048;
}
