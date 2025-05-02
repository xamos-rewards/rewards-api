package org.xamos.rewards.exceptions;

public class IdNotFoundException extends RuntimeException {

  public IdNotFoundException() {
    super();
  }

  public IdNotFoundException(String resourceType, Long id, Throwable cause) {
    super(resourceType + " By Id: " + id + " Not Found", cause);
  }

  public IdNotFoundException(String resourceType, Long id) {
    super(resourceType + " By Id: " + id + " Not Found");
  }
}
