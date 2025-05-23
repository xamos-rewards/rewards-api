package org.xamos.rewards.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiValidationError extends ApiSubError {

  private String object;
  private String field;
  private Object rejectedValue;
  private String message;

  public ApiValidationError(String object, String message) {
    this.object = object;
    this.message = message;
  }
}
