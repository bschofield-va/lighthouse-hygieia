package gov.va.api.lighthouse.hygieia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class GenericErrorResponseExceptionHandlerTest {

  ExceptionHandlerVerifier<GenericErrorResponseExceptionHandler> verifier;

  @BeforeEach
  void _createHandler() {
    verifier = ExceptionHandlerVerifier.create(GenericErrorResponseExceptionHandler::new);
  }

  @Test
  void handleBadRequest() {
    verifier.assertHandled(HttpStatus.BAD_REQUEST, verifier.handler()::handleBadRequest);
  }

  @Test
  void handleNoHandlerFound() {
    verifier.assertHandled(HttpStatus.NOT_FOUND, verifier.handler()::handleNoHandlerFound);
  }

  @Test
  void handleSnafu() {
    verifier.assertHandled(HttpStatus.INTERNAL_SERVER_ERROR, verifier.handler()::handleSnafu);
  }
}
