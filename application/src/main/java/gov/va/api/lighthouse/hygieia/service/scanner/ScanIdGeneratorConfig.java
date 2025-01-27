package gov.va.api.lighthouse.hygieia.service.scanner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScanIdGeneratorConfig {
  private String idOf(Object o) {
    return String.format("%06X", System.identityHashCode(o) & 0XFFFFFF);
  }

  @Bean
  ScanIdGenerator scanIdGenerator() {
    final var counter = new AtomicLong(0);
    final var applicationInstance = idOf(this);
    return () ->
        applicationInstance
            + "-"
            + idOf(Thread.currentThread())
            + "-"
            + counter.incrementAndGet()
            + "-"
            + ZonedDateTime.now(ZoneId.of("GMT"))
                .withNano(0)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }
}
