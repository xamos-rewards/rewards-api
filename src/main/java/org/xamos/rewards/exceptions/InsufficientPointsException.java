package org.xamos.rewards.exceptions;

public class InsufficientPointsException extends RuntimeException {

  public InsufficientPointsException() {
  }

  public InsufficientPointsException(String message) {
    super(message);
  }

  public InsufficientPointsException(String message, Throwable cause) {
    super(message, cause);
  }

  public InsufficientPointsException(Throwable cause) {
    super(cause);
  }

  public InsufficientPointsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
