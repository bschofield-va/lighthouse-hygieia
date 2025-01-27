package gov.va.api.lighthouse.hygieia.service.config.encryption;

@FunctionalInterface
public interface EncryptionService {
  /** Encrypt a message suitable for logging or sensitive responses, such as exception messages. */
  String encrypt(String string);
}
