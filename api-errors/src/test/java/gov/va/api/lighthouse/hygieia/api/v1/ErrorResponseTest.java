package gov.va.api.lighthouse.hygieia.api.v1;

import static gov.va.api.lighthouse.hygieia.api.v1.RoundTrip.assertRoundTrip;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse.ErrorDetail;
import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse.Meta;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  ErrorDetail _detail(String seed) {
    return ErrorDetail.builder()
        .id("id-" + seed)
        .status("status-" + seed)
        .code("code-" + seed)
        .detail("detail-" + seed)
        .meta(Meta.builder().time(Instant.now()).diagnostics("diagnostics-" + seed).build())
        .build();
  }

  @Test
  void roundTrip() {
    assertRoundTrip(ErrorResponse.builder().errors(List.of(_detail("1"), _detail("2"))).build());
  }
}
