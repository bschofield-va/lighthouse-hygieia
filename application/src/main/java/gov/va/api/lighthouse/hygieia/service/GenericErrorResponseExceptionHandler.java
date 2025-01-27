package gov.va.api.lighthouse.hygieia.service;

import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory.ErrorContext;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.UndeclaredThrowableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class GenericErrorResponseExceptionHandler {

  private final ErrorResponseFactory errorResponses;

  @ExceptionHandler({
    BindException.class,
    HttpMediaTypeException.class,
    HttpRequestMethodNotSupportedException.class,
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class,
    UnsatisfiedServletRequestParameterException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse handleBadRequest(Exception e, HttpServletRequest request) {
    return errorResponses.create(
        ErrorContext.builder()
            .status(HttpStatus.BAD_REQUEST)
            .request(request)
            .exception(e)
            .build());
  }

  @ExceptionHandler({NoHandlerFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse handleNoHandlerFound(Exception e, HttpServletRequest request) {
    return errorResponses.create(
        ErrorContext.builder().status(HttpStatus.NOT_FOUND).request(request).exception(e).build());
  }

  @ExceptionHandler({Exception.class, UndeclaredThrowableException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  ErrorResponse handleSnafu(Exception e, HttpServletRequest request) {
    log.error("Unexpected Exception: " + e.getMessage(), e);
    return errorResponses.create(
        ErrorContext.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .request(request)
            .exception(e)
            .build());
  }
}
