package gov.va.api.lighthouse.hygieia.api.v1.test;

import gov.va.api.lighthouse.hygieia.api.v1.ScanResponseV1;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "get")
public class V1Data {
  public ScanResponseV1 scanResponse(boolean virusFound, String virusName) {
    return ScanResponseV1.builder()
        .data(ScanResponseV1.Data.builder().virusFound(virusFound).virusName(virusName).build())
        .build();
  }
}
