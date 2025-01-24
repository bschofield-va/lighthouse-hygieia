package gov.va.api.lighthouse.hygieia.service.config;

import java.util.stream.Stream;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourcePathMappingConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    Stream.of("/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness")
        .forEach(
            healthCheck -> {
              registry.addViewController("/v1" + healthCheck).setViewName(healthCheck);
            });
    registry.addViewController("/v1/openapi.json").setViewName("/api-docs/v1");
  }
}
