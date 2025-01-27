package gov.va.api.lighthouse.hygieia.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE)
@Schema(
    description = "Creation response",
    requiredProperties = {"data"})
public class ScanResponseV1 {
  @NotNull @Valid ScanResponseV1.Data data;

  @Value
  @Builder
  @Jacksonized
  @JsonAutoDetect(
      fieldVisibility = JsonAutoDetect.Visibility.ANY,
      isGetterVisibility = JsonAutoDetect.Visibility.NONE)
  @Schema(
      name = "ScanResponseV1.Data",
      requiredProperties = {"trackingId", "virusFound"})
  public static class Data {

    @Schema(
        description =
            """
            An ID that can be used to track the scan request if necessary.
            """)
    @NotEmpty
    String scanId;

    @Schema(
        description =
            """
            If true, a virus has been found and additional information will be provided in the
            response.
            """)
    @NotNull
    boolean virusFound;

    @Schema(description = "The name of virus if found.", example = "EICAR")
    String virusName;
  }
}
