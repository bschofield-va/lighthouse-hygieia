package gov.va.api.lighthouse.hygieia.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PathRewriteConfigTest {

  @Test
  void beanHandlesExemplar() {
    var filter = new PathRewriteConfig().pathRewriteFilter().getFilter();
    assertThat(filter.removeLeadingPathsAsUrlPatterns()).containsExactlyInAnyOrder("/hygieia/*");
  }
}
