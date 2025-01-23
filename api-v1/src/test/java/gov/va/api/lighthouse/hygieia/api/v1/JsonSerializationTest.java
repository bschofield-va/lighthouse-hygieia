package gov.va.api.lighthouse.hygieia.api.v1;

import static gov.va.api.lighthouse.hygieia.api.v1.RoundTrip.assertRoundTrip;

import gov.va.api.lighthouse.hygieia.api.v1.test.V1Data;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JsonSerializationTest {

  static Stream<Arguments> roundTrip() {
    var data = V1Data.get();
    return Stream.of(
            data.scanResponse(true, "EICAR")
            //
            )
        .map(o -> Arguments.of(o.getClass().getSimpleName(), o));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource
  void roundTrip(String type, Object object) {
    assertRoundTrip(object);
  }
}
