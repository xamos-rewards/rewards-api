package org.xamos.rewards.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.xamos.rewards.exceptions.ApplicationIdNotFoundException;
import org.xamos.rewards.exceptions.InsufficientPointsException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private Mono<ResponseEntity<ApiError>> buildResponseEntity(ApiError apiError) {
    return Mono.just(new ResponseEntity<>(apiError, apiError.getStatus()));
  }

  @ExceptionHandler(InsufficientPointsException.class)
  public Mono<ResponseEntity<ApiError>> handleInsufficientPointsException(ServerWebExchange exchange, InsufficientPointsException insufficientPointsException) {
    log.error("{} Request to {}, user has insufficient points for operation", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), insufficientPointsException);

    String errorMessage = "User has insufficient points for operation";
    ApiError apiError = new ApiError(HttpStatus.CONFLICT, errorMessage);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ApplicationIdNotFoundException.class)
  public Mono<ResponseEntity<ApiError>> handleApplicationIdNotFoundException(ServerWebExchange exchange, ApplicationIdNotFoundException applicationIdNotFoundException) {
    log.error("{} Request to {}, Requested application not found", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), applicationIdNotFoundException);

    String errorMessage = "Application not found";
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, errorMessage, applicationIdNotFoundException);

    return buildResponseEntity(apiError);
  }

  @Override
  protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
    log.error("{} Request to {}, Invalid data format", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), ex);

    String errorMessage = "Invalid data format";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, ex);
    apiError.setDebugMessage(ex.getReason());
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> apiError.addValidationError(fieldError));
    ex.getBindingResult().getGlobalErrors().forEach(globalError -> apiError.addValidationError(globalError));

    // This mapping is used to simplify the conversion of types
    // and avoid the need for casting
    return buildResponseEntity(apiError).map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
  }

  @Override
  protected Mono<ResponseEntity<Object>> handleServerWebInputException(ServerWebInputException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
    log.error("{} Request to {} provided invalid JSON", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), ex);

    String errorMessage = "Invalid JSON format";
    String debugMessage = (ex.getCause() != null) ? ex.getCause().getLocalizedMessage() : ex.getReason();
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, debugMessage);

    return buildResponseEntity(apiError).map(response -> ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
  }

  // Placeholder for Spring Security Configuration
//  @ExceptionHandler(AccessDeniedException.class)
//  public ResponseEntity<Object> handleAccessDeniedException(ServerWebExchange exchange, AccessDeniedException accessDeniedException) {
//    log.error("{} Request to {}, action not permitted by user", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), accessDeniedException);
//
//    String errorMessage = "Action not permitted";
//    ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, errorMessage, accessDeniedException);
//
//    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
//  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public Mono<ResponseEntity<ApiError>> handleDataIntegrityViolationException(ServerWebExchange exchange, DataIntegrityViolationException dataIntegrityViolationException) {
    log.error("{} Request to {}, Data integrity violation", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), dataIntegrityViolationException);

    String errorMessage = "Data integrity violation";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, dataIntegrityViolationException);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(TransientDataAccessResourceException.class)
  public Mono<ResponseEntity<ApiError>> handleTransientDataAccessResourceException(ServerWebExchange exchange, TransientDataAccessResourceException transientDataAccessResourceException) {
    log.error("{} Request to {}, Data access failure", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), transientDataAccessResourceException);

    // We exclude the debugMessage from the ApiError as it can be too revealing of internal details
    String errorMessage = "Data access failure";
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ApiError>> handleIllegalArgumentException(ServerWebExchange exchange, IllegalArgumentException illegalArgumentException) {
    log.error("{} Request to {} provided an invalid request", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), illegalArgumentException);

    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, illegalArgumentException.getMessage(), illegalArgumentException);

    return buildResponseEntity(apiError);
  }

  @Override
  protected Mono<ResponseEntity<Object>> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
    log.error("{} Request to {} raised {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), ex, ex);

    return super.handleExceptionInternal(ex, body, headers, status, exchange);
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ApiError>> handleGenericException(ServerWebExchange exchange, Exception exception) {
    log.error("{} Request to {} raised {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), exception, exception);

    String errorMessage = "Something went wrong";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, exception);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(Error.class)
  public Mono<ResponseEntity<ApiError>> handleGenericError(ServerWebExchange exchange, Error error) {
    log.error("{} Request to {} raised {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), error, error);

    String errorMessage = "Fatal internal server error occurred";
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, error);

    return buildResponseEntity(apiError);
  }
}
