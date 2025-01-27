package gov.va.api.lighthouse.hygieia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import io.restassured.RestAssured;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("marbles")
@Execution(SAME_THREAD)
public class AllTheMarblesTest {
  @LocalServerPort long port;

  @Test
  void exceptionHandlingIsConfigured() {
    //    var response =
    //        RestAssured.given()
    //            .contentType(ContentType.JSON)
    //            .header(new Header("client-key", "test-client-key"))
    //            .get(
    //                "http://localhost:" + port + "/hygieia/v1/systems/fugazi-" + new
    // Random().nextInt())
    //            .then()
    //            .log()
    //            .ifError()
    //            .and()
    //            .assertThat()
    //            .statusCode(404)
    //            .extract()
    //            .as(ErrorResponse.class);
    //    assertThat(response.errors().get(0).status()).isEqualTo("404");
    //    assertThat(response.errors().get(0).code()).isEqualTo("UnknownSystem");
  }

  @Test
  void inputValidationIsApplied() {
    var response =
        RestAssured.given()
            .multiPart("nope", "data".getBytes(StandardCharsets.UTF_8))
            .post("http://localhost:" + port + "/hygieia/v1/scanner")
            .then()
            .log()
            .ifError()
            .and()
            .assertThat()
            .statusCode(400)
            .extract()
            .as(ErrorResponse.class);
    assertThat(response.errors().get(0).status()).isEqualTo("400");
  }

  @ParameterizedTest
  @ValueSource(strings = {"v1"})
  void openApiIsAvailable(String version) {
    var reportedVersion =
        RestAssured.get("http://localhost:" + port + "/hygieia/" + version + "/openapi.json")
            .then()
            .log()
            .ifError()
            .and()
            .assertThat()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("info.version");
    assertThat(reportedVersion).isEqualTo(version);
  }

  @Test
  void securityHeadersArePresent() {
    RestAssured.get("http://localhost:" + port + "/hygieia/v1/openapi.json")
        .then()
        .log()
        .ifError()
        .and()
        .assertThat()
        .header("X-Content-Type-Options", "nosniff")
        .header("X-XSS-Protection", notNullValue())
        .header("Cache-Control", containsString("no-cache"))
        .header("Pragma", containsString("no-cache"))
        .header("Strict-Transport-Security", notNullValue())
        .header("X-Frame-Options", "DENY");
  }
}
