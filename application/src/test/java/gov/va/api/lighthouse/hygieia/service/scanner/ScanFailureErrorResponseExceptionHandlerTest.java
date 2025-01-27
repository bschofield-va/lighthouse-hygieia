package gov.va.api.lighthouse.hygieia.service.scanner;

import gov.va.api.lighthouse.hygieia.service.ExceptionHandlerVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ScanFailureErrorResponseExceptionHandlerTest {

  ExceptionHandlerVerifier<ScanFailureErrorResponseExceptionHandler> verifier;

  @BeforeEach
  void _createHandler() {
    verifier = ExceptionHandlerVerifier.create(ScanFailureErrorResponseExceptionHandler::new);
  }

  @Test
  void handleBadRequest() {
    verifier.assertHandled(
        HttpStatus.INTERNAL_SERVER_ERROR, verifier.handler()::handleVirusScanningFailure);
  }
}
