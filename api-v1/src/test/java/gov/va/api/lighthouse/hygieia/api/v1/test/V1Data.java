package gov.va.api.lighthouse.hygieia.api.v1.test;

import gov.va.api.lighthouse.hygieia.api.v1.ScanResponseV1;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "get")
public class V1Data {
  public ScanResponseV1 cleanScanResponse(String scanId) {
    return scanResponse(scanId, false, null);
  }

  public ScanResponseV1 scanResponse(String scanId, boolean virusFound, String virusName) {
    return ScanResponseV1.builder()
        .data(
            ScanResponseV1.Data.builder()
                .scanId(scanId)
                .virusFound(virusFound)
                .virusName(virusName)
                .build())
        .build();
  }

  public ScanResponseV1 virusFoundScanResponse(String scanId, String virusName) {
    return scanResponse(scanId, true, virusName);
  }
}
