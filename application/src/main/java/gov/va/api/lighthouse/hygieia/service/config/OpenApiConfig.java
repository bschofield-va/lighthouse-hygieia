package gov.va.api.lighthouse.hygieia.service.config;

import gov.va.api.lighthouse.hygieia.service.config.OpenApiProperties.ApiDetails;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class OpenApiConfig {

  private final OpenApiProperties openApiProperties;

  @Bean
  GroupedOpenApi v1() {
    return GroupedOpenApi.builder()
        .group("v1")
        .pathsToMatch("/v1/**")
        .addOpenApiCustomizer(
            EnvironmentSpecificDetails.builder().details(openApiProperties.v1()).build())
        .build();
  }

  private static class EnvironmentSpecificDetails implements OpenApiCustomizer {
    private final ApiDetails details;

    @Builder
    EnvironmentSpecificDetails(ApiDetails details) {
      this.details = details;
    }

    @Override
    public void customise(OpenAPI openApi) {
      replaceInfo(openApi);
      replaceServers(openApi);
      replaceSecuritySchemes(openApi);
    }

    private void replaceInfo(OpenAPI openApi) {
      if (details.info() == null) {
        return;
      }
      openApi.info(details.info());
    }

    private void replaceSecuritySchemes(OpenAPI openApi) {
      var scheme = details.securityScheme();
      if (scheme == null) {
        return;
      }
      var key = scheme.getType().toString().toLowerCase(Locale.US);
      openApi.getComponents().setSecuritySchemes(Map.of(key, scheme));
    }

    private void replaceServers(OpenAPI openApi) {
      if (details.servers().isEmpty()) {
        return;
      }
      openApi.setServers(details.servers());
    }
  }
}
