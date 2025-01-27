package gov.va.api.lighthouse.hygieia.its;

import gov.va.api.health.sentinel.ReducedSpamLogger;
import gov.va.api.health.sentinel.configurablevalues.ConfigurableValues;
import lombok.Builder;
import lombok.Value;
import org.slf4j.LoggerFactory;

@Value
@Builder
public class SystemDefinition {

  static ReducedSpamLogger log =
      ReducedSpamLogger.builder().logger(LoggerFactory.getLogger(SystemDefinition.class)).build();

  String baseUrl;
  int port;
  String apiPath;

  /** Get the systemd definition populated from the environment. */
  public static SystemDefinition get() {
    var values = ConfigurableValues.get();
    var protocol = values.forPropertyName("deployment.test.protocol").orElse("http").asString();
    var host = values.forPropertyName("deployment.test.host").asString();
    var port = values.forPropertyName("deployment.test.port").asInteger();
    var apiPath = values.forPropertyName("deployment.test.api-path").orElse("/hygieia").asString();

    SystemDefinition definition =
        SystemDefinition.builder()
            .baseUrl(protocol + "://" + host)
            .port(port)
            .apiPath(apiPath)
            .build();

    log.infoOnce("{}", definition);
    return definition;
  }
}
