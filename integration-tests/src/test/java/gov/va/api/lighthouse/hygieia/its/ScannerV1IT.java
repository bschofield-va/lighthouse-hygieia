package gov.va.api.lighthouse.hygieia.its;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.hygieia.api.v1.ScanResponseV1;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ScannerV1IT extends EndpointValidation {

  @Test
  void scanClean() {
    var response =
        endpoints()
            .v1()
            .multiPart(
                "file", "fake-clean-file", "fugazi".repeat(10).getBytes(StandardCharsets.UTF_8))
            .when()
            .post("/scanner")
            .then()
            .assertThat()
            .statusCode(200);
    assertThat(response).satisfies(jsonValidation());
    var scanResponse = response.extract().as(ScanResponseV1.class);
    assertThat(scanResponse.data().virusFound()).isFalse();
  }

  @Test
  void scanInfected() {

    var eicar = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

    var response =
        endpoints()
            .v1()
            .multiPart("file", "fake-infected-file", eicar.getBytes(StandardCharsets.UTF_8))
            .post("/scanner")
            .then()
            .assertThat()
            .statusCode(200);
    assertThat(response).satisfies(jsonValidation());
    var scanResponse = response.extract().as(ScanResponseV1.class);
    assertThat(scanResponse.data().virusFound()).isTrue();
    assertThat(scanResponse.data().virusName()).isNotBlank();
  }
}
