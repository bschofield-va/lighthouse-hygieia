package gov.va.api.lighthouse.hygieia.service.config;

import gov.va.api.lighthouse.talos.PathRewriteFilter;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PathRewriteConfig {
  @Bean
  FilterRegistrationBean<PathRewriteFilter> pathRewriteFilter() {
    var registration = new FilterRegistrationBean<PathRewriteFilter>();
    PathRewriteFilter filter =
        PathRewriteFilter.builder().removeLeadingPath(List.of("/hygieia/")).build();
    registration.setFilter(filter);
    registration.addUrlPatterns(filter.removeLeadingPathsAsUrlPatterns());
    log.info(
        "Removing leading paths: {}", Arrays.toString(filter.removeLeadingPathsAsUrlPatterns()));
    return registration;
  }
}
