package gov.va.api.lighthouse.hygieia.service.scanner;

import java.util.function.Supplier;

public interface ScanIdGenerator extends Supplier<String> {
  String get();
}
