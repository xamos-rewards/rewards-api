package org.xamos.rewards.exceptions;

public class ApplicationClientIdNotFoundException extends RuntimeException {
  public ApplicationClientIdNotFoundException(String clientId) {
    super("Application with clientId " + clientId + " not found");
  }

  public ApplicationClientIdNotFoundException(String clientId, Throwable cause) {
    super("Application with clientId " + clientId + " not found", cause);
  }
}
