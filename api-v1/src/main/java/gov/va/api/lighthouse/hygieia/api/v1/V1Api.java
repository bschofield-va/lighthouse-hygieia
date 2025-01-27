package gov.va.api.lighthouse.hygieia.api.v1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.web.bind.annotation.RequestMapping;

@OpenAPIDefinition
@RequestMapping(
    path = "/v1",
    produces = {"application/json"})
public interface V1Api {}
