package gov.va.api.lighthouse.hygieia.its;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ErrorResponseIT extends EndpointValidation {

  @ParameterizedTest
  @ValueSource(strings = {"/fugazi", "/v1/fugazi", "/v2/fugazi"})
  void errorResponses(String badPath) {
    var response =
        endpoints()
            .baseUrl()
            .get(badPath)
            .then()
            .assertThat()
            .statusCode(404)
            .extract()
            .as(ErrorResponse.class);
    assertThat(response).satisfies(jsonValidation());
    assertThat(response.errors().size()).isEqualTo(1);
  }
}
