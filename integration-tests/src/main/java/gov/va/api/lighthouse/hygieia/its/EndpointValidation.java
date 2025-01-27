package gov.va.api.lighthouse.hygieia.its;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointValidation {

  protected static <T> Consumer<? super T> jsonValidation() {
    return (T payload) -> {
      try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
        Set<ConstraintViolation<T>> violations = validatorFactory.getValidator().validate(payload);
        if (violations.isEmpty()) {
          return;
        }
        StringBuilder message = new StringBuilder("Constraint Violations:");
        violations.forEach(
            (v) -> {
              message
                  .append('\n')
                  .append(v.getMessage())
                  .append(": ")
                  .append(v.getPropertyPath().toString())
                  .append(" = ")
                  .append(v.getInvalidValue());
            });
        message.append("\n\nDetails:");
        violations.forEach(
            (v) -> {
              message.append('\n').append(v);
            });
        writeQuietly(payload);
        throw new AssertionError(message.toString());
      }
    };
  }

  @SneakyThrows
  protected static void writeQuietly(Object payload) {
    new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, payload);
  }

  public Endpoints endpoints() {
    return new Endpoints(SystemDefinition.get(), LoggerFactory.getLogger(getClass()));
  }

  @RequiredArgsConstructor
  public static class Endpoints {

    @Getter private final SystemDefinition definition;

    private final Logger log;

    /** Return a specification ready to use on the v1 API path. */
    public RequestSpecification baseUrl() {
      return requestFor("");
    }

    private RequestSpecification requestFor(String apiVersion) {
      return RestAssured.given()
          .relaxedHTTPSValidation()
          .filter(
              (req, res, ctx) -> {
                log.info("{} {}", req.getMethod(), req.getURI());
                return ctx.next(req, res);
              })
          .baseUri(definition().baseUrl())
          .port(definition().port())
          .basePath(definition().apiPath() + apiVersion)
          .accept(ContentType.JSON)
          .log()
          .ifValidationFails(LogDetail.URI);
    }

    /** Return a specification ready to use on the v1 API path. */
    public RequestSpecification v1() {
      return requestFor("/v1");
    }
  }
}
