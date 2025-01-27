package gov.va.api.lighthouse.hygieia.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class RoundTrip {
  @SneakyThrows
  public static void assertRoundTrip(Object object) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    Object evilTwin = mapper.readValue(json, object.getClass());
    assertThat(evilTwin).isEqualTo(object);
  }
}
