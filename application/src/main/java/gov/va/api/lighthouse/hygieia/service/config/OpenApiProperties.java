package gov.va.api.lighthouse.hygieia.service.config;

import static java.util.Objects.requireNonNullElseGet;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(value = "open-api", ignoreUnknownFields = false)
@AllArgsConstructor
@Builder
public class OpenApiProperties {
  private final ApiDetails v1;

  @Data
  @Validated
  public static class ApiDetails {
    private final Info info;
    private final SecurityScheme securityScheme;
    private final List<Server> servers;

    /** Create instant that emulates part of OpenAPI model. */
    public ApiDetails(Info info, SecurityScheme securityScheme, List<Server> servers) {
      this.info = info;
      this.securityScheme = securityScheme;
      this.servers = requireNonNullElseGet(servers, List::of);
    }
  }
}
