package gov.va.api.lighthouse.hygieia.service.scanner;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import gov.va.api.lighthouse.hygieia.service.antivirus.VirusScanner.VirusScanningException;
import gov.va.api.lighthouse.hygieia.service.clamav.ClamAvExceptions.FailedToConnectToClamAvServer;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory.ErrorContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ScanFailureErrorResponseExceptionHandler {

  private final ErrorResponseFactory errorResponses;

  @ExceptionHandler({VirusScanningException.class, FailedToConnectToClamAvServer.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  ErrorResponse handleVirusScanningFailure(Exception e, HttpServletRequest request) {
    log.error("Scan failure: {}", e.getMessage(), e);
    return errorResponses.create(
        ErrorContext.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .request(request)
            .exception(e)
            .build());
  }
}
