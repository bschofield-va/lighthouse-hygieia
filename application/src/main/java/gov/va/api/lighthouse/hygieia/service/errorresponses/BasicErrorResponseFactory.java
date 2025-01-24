package gov.va.api.lighthouse.hygieia.service.errorresponses;

import static gov.va.api.lighthouse.hygieia.service.errorresponses.BasicErrorResponseFactory.Exceptions.extractSafeMessage;
import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse;
import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse.ErrorDetail;
import gov.va.api.lighthouse.hygieia.api.errors.ErrorResponse.Meta;
import gov.va.api.lighthouse.hygieia.service.HasSafeMessage;
import gov.va.api.lighthouse.hygieia.service.config.encryption.EncryptionService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
public class BasicErrorResponseFactory implements ErrorResponseFactory {
  private final EncryptionService encryption;

  private final ObjectMapper mapper;

  private static String codeOf(ErrorContext context) {
    var code = context.exception().getClass().getSimpleName();
    if (code.endsWith("Exception")) {
      code = code.replace("Exception", "");
    }
    return code;
  }

  private static <E, C extends Collection<E>> C emptyToNull(C collection) {
    return (collection == null || collection.isEmpty()) ? null : collection;
  }

  @Override
  public ErrorResponse create(ErrorContext context) {
    return ErrorResponse.builder()
        .errors(
            List.of(
                ErrorDetail.builder()
                    .id(UUID.randomUUID().toString())
                    .status(ifPresent(context.status(), s -> Integer.toString(s.value())))
                    .code(codeOf(context))
                    .title(context.status().getReasonPhrase())
                    .detail(extractSafeMessage(context.exception()))
                    .meta(
                        Meta.builder()
                            .time(Instant.now())
                            .diagnostics(createDiagnostic(context))
                            .build())
                    .build()))
        .build();
  }

  @SneakyThrows
  private String createDiagnostic(ErrorContext context) {
    var diagnostics =
        Diagnostics.builder()
            .timestamp(Instant.now())
            .requestUrl(reconstructUrl(context.request()))
            .notes(emptyToNull(context.notes()))
            .exceptionType(context.exception().getClass().getName())
            .exceptionMessage(Exceptions.sanitizedMessage(context.exception()))
            .causes(emptyToNull(Exceptions.causeSummary(context.exception())))
            .build();
    var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagnostics);
    return encryption.encrypt(json);
  }

  private <T> String ifPresent(T value, Function<T, String> converter) {
    if (value == null) {
      return null;
    }
    return converter.apply(value);
  }

  private String reconstructUrl(HttpServletRequest request) {
    return request.getRequestURI()
        + (request.getQueryString() == null ? "" : "?" + request.getQueryString())
            .replaceAll("[\r\n]", "");
  }

  @Builder(toBuilder = true)
  @Jacksonized
  @Value
  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  public static class Diagnostics {
    Instant timestamp;

    String exceptionType;

    String exceptionMessage;

    List<String> causes;

    String requestUrl;

    List<String> notes;
  }

  static class Exceptions {
    private static final List<Class<?>> EXCEPTIONS_WITH_SAFE_MESSAGES =
        List.of(
            HttpClientErrorException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingRequestValueException.class,
            ServletRequestBindingException.class);

    static List<String> causeSummary(Throwable tr) {
      return causes(tr).stream()
          .map(t -> t.getClass().getSimpleName() + " " + Exceptions.sanitizedMessage(t))
          .toList();
    }

    static List<Throwable> causes(Throwable tr) {
      List<Throwable> results = new ArrayList<>();
      Throwable current = tr;
      while (true) {
        current = current.getCause();
        if (current == null) {
          return results;
        }
        results.add(current);
      }
    }

    static String extractSafeMessage(Throwable tr) {
      for (Class<?> safeType : EXCEPTIONS_WITH_SAFE_MESSAGES) {
        if (safeType.isInstance(tr)) {
          return tr.getMessage();
        }
      }
      if (tr.getClass().isAnnotationPresent(HasSafeMessage.class)) {
        return tr.getMessage();
      }
      return null;
    }

    static String sanitizedMessage(Throwable tr) {
      if (tr instanceof JsonMappingException jme) {
        return format("path: %s", jme.getPathReference());
      }
      if (tr instanceof JsonParseException jpe) {
        if (jpe.getLocation() != null) {
          return format(
              "line: %s, column: %s",
              jpe.getLocation().getLineNr(), jpe.getLocation().getColumnNr());
        }
      }
      return tr.getMessage();
    }
  }
}
