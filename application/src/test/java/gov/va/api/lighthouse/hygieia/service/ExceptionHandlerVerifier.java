package gov.va.api.lighthouse.hygieia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse.ErrorDetail;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory.ErrorContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

@Getter
public class ExceptionHandlerVerifier<T> {

  private final ErrorResponseFactory factory;

  private final T handler;

  private final ArgumentCaptor<ErrorContext> captor;

  public ExceptionHandlerVerifier(Function<ErrorResponseFactory, T> createHandler) {
    captor = ArgumentCaptor.forClass(ErrorContext.class);
    factory = mock(ErrorResponseFactory.class);
    when(factory.create(captor.capture()))
        .thenReturn(ErrorResponse.builder().errors(List.of(ErrorDetail.builder().build())).build());
    handler = createHandler.apply(factory);
  }

  public static <T> ExceptionHandlerVerifier<T> create(
      Function<ErrorResponseFactory, T> createHandler) {
    return new ExceptionHandlerVerifier<>(createHandler);
  }

  public void assertHandled(
      HttpStatus expectedStatus,
      BiFunction<Exception, HttpServletRequest, ErrorResponse> handlerMethod) {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/fugazi/1");
    var exception = new FugaziException();
    var response = handlerMethod.apply(exception, request);
    assertThat(response).isNotNull();
    var expectedContext =
        ErrorContext.builder().status(expectedStatus).request(request).exception(exception).build();
    assertThat(captor.getValue()).isEqualTo(expectedContext);
  }

  private static class FugaziException extends RuntimeException {}
}
