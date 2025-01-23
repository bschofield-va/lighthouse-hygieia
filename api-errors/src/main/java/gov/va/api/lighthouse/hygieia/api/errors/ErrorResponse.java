package gov.va.api.lighthouse.hygieia.api.errors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "API invocation or processing error.")
public class ErrorResponse {

  @Schema(description = "One or more error details.")
  @NonNull
  @NotNull
  @Size(min = 1)
  List<ErrorDetail> errors;

  @Value
  @Builder
  @Jacksonized
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(description = "Details about the error.")
  public static class ErrorDetail {
    @Schema(description = "A unique identifier for this particular occurrence of the problem.")
    String id;

    @Schema(description = "The HTTP status code, expressed as a string.")
    String status;

    @Schema(description = "The application specific error code.")
    String code;

    @Schema(description = "A short, human readable summary of the problem.")
    String title;

    @Schema(description = "A human readable explanation specific to this occurrence of the problem")
    String detail;

    @Schema(description = "Meta-information about the error.")
    Meta meta;
  }

  @Value
  @Builder
  @Jacksonized
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(description = "Meta-information about the error.")
  public static class Meta {
    @Schema(description = "The time the error occurred.")
    Instant time;

    @Schema(
        description = "Error diagnostic payload. Include this information with any support ticket.")
    String diagnostics;
  }
}
