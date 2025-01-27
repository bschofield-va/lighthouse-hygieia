package gov.va.api.lighthouse.hygieia.service.errorresponses;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpStatus;

public interface ErrorResponseFactory {

  ErrorResponse create(ErrorContext context);

  @Value
  @Builder
  class ErrorContext {
    @NonNull HttpServletRequest request;
    @NonNull HttpStatus status;
    @NonNull Throwable exception;
    List<String> notes;
  }
}
