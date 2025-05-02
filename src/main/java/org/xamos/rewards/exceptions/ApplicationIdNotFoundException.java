package org.xamos.rewards.exceptions;

public class ApplicationIdNotFoundException extends IdNotFoundException {
  private static final String RESOURCE_TYPE = "Application";

  public ApplicationIdNotFoundException() {
    super();
  }

  public ApplicationIdNotFoundException(Long id, Throwable cause) {
    super(RESOURCE_TYPE, id, cause);
  }

  public ApplicationIdNotFoundException(Long id) {
    super(RESOURCE_TYPE, id);
  }
}
