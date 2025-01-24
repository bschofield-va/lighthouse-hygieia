package gov.va.api.lighthouse.hygieia.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Services")
public interface ScannerV1Api extends V1Api {
  @Operation(
      description =
          """
    Scan a file.
    """)
  @PostMapping(
      path = {"/scanner"},
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ScanResponseV1 scan(@RequestParam(name = "file", required = true) MultipartFile file);
}
