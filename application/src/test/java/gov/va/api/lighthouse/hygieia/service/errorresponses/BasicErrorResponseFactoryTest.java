package gov.va.api.lighthouse.hygieia.service.errorresponses;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.hygieia.service.errorresponses.BasicErrorResponseFactory.Diagnostics;
import gov.va.api.lighthouse.hygieia.service.errorresponses.BasicErrorResponseFactory.Exceptions;
import gov.va.api.lighthouse.hygieia.service.errorresponses.ErrorResponseFactory.ErrorContext;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingRequestValueException;

class BasicErrorResponseFactoryTest {

  ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  BasicErrorResponseFactory _factory() {
    return new BasicErrorResponseFactory(s -> s, mapper);
  }

  @SneakyThrows
  Exception _forceJacksonException(String badJson) {
    try {
      mapper.readValue(badJson, Diagnostics.class);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  private Exception _jsonMappingException() {
    return _forceJacksonException(
        """
        { "timestamp": { "nope": "nope" } } }
        """);
  }

  private Exception _jsonParseException() {
    return _forceJacksonException(
        """
        { "nope":
        """);
  }

  @Test
  void causes() {
    Assertions.assertThat(Exceptions.causes(new IllegalArgumentException("no cause"))).isEmpty();

    var e0 = new RuntimeException("the root");
    var e1 = new RuntimeException(e0);
    var e2 = new RuntimeException(e1);
    var e3 = new RuntimeException(e2);

    assertThat(Exceptions.causes(new IllegalArgumentException("some causes", e3)))
        .containsExactly(e3, e2, e1, e0);
  }

  @Test
  @SneakyThrows
  void createPopulatesExceptionInformation() {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/fugazi/1");
    request.setQueryString("mode=awesome");
    var exception = new MissingRequestValueException("fugazi value is missing");
    var before = Instant.now();
    var actual =
        _factory()
            .create(
                ErrorContext.builder()
                    .request(request)
                    .status(HttpStatus.I_AM_A_TEAPOT)
                    .exception(exception)
                    .notes(List.of("tacos burritos nachos", "mmm mm good"))
                    .build());
    var after = Instant.now();
    assertThat(actual.errors().size()).isEqualTo(1);

    var detail = actual.errors().get(0);
    assertThat(detail.id()).isNotBlank();
    assertThat(detail.status()).isEqualTo("418");
    assertThat(detail.title()).isEqualTo("I'm a teapot");
    assertThat(detail.code()).isNotBlank();
    assertThat(detail.detail()).isEqualTo("fugazi value is missing");
    assertThat(detail.meta().time()).isBetween(before, after);

    var diagnostics = mapper.readValue(detail.meta().diagnostics(), Diagnostics.class);
    assertThat(diagnostics.timestamp()).isBetween(before, after);
    assertThat(diagnostics.exceptionType()).isEqualTo(exception.getClass().getName());
    assertThat(diagnostics.exceptionMessage()).isEqualTo(exception.getMessage());
    assertThat(diagnostics.notes()).isNotEmpty();
    // no root causes for this exception
    assertThat(diagnostics.causes()).isNullOrEmpty();
    assertThat(diagnostics.requestUrl()).isEqualTo("/fugazi/1?mode=awesome");
  }

  @Test
  void extractSafeMessage() {
    assertThat(Exceptions.extractSafeMessage(new MissingRequestValueException("i'm safe")))
        .isEqualTo("i'm safe");
    assertThat(Exceptions.extractSafeMessage(new IllegalArgumentException("i'm not safe")))
        .isNull();
  }

  @SneakyThrows
  @Test
  void sanitizedMessage() {
    assertThat(Exceptions.sanitizedMessage(_jsonParseException())).matches("line: .*, column: .*");
    assertThat(Exceptions.sanitizedMessage(_jsonMappingException())).matches("path: .*");
  }
}
