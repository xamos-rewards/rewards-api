package org.xamos.rewards.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.xamos.rewards.exceptions.ApplicationClientIdNotFoundException;
import org.xamos.rewards.exceptions.ApplicationIdNotFoundException;
import org.xamos.rewards.exceptions.Auth0ManagementException;
import org.xamos.rewards.exceptions.InsufficientPointsException;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(Auth0ManagementException.class)
  public ResponseEntity<ApiError> handleAuth0ManagementException(HttpServletRequest request, Auth0ManagementException ex) {
    log.error("{} Request to {}, Auth0 Management API error: {}", 
              request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

    ApiError apiError = new ApiError(HttpStatus.BAD_GATEWAY, "Error communicating with identity provider", ex);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(InsufficientPointsException.class)
  public ResponseEntity<ApiError> handleInsufficientPointsException(HttpServletRequest request, InsufficientPointsException insufficientPointsException) {
    log.error("{} Request to {}, user has insufficient points for operation", request.getMethod(), request.getRequestURI(), insufficientPointsException);

    String errorMessage = "User has insufficient points for operation";
    ApiError apiError = new ApiError(HttpStatus.CONFLICT, errorMessage, insufficientPointsException);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ApplicationIdNotFoundException.class)
  public ResponseEntity<ApiError> handleApplicationIdNotFoundException(HttpServletRequest request, ApplicationIdNotFoundException applicationIdNotFoundException) {
    log.error("{} Request to {}, Requested application not found", request.getMethod(), request.getRequestURI(), applicationIdNotFoundException);

    String errorMessage = "Application not found";
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, errorMessage, applicationIdNotFoundException);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ApplicationClientIdNotFoundException.class)
  public ResponseEntity<ApiError> handleApplicationClientIdNotFoundException(HttpServletRequest request, ApplicationClientIdNotFoundException applicationClientIdNotFoundException) {
    log.error("{} Request to {}, Requested application with client ID not found", request.getMethod(), request.getRequestURI(), applicationClientIdNotFoundException);

    String errorMessage = "Application with client ID not found";
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, errorMessage, applicationClientIdNotFoundException);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException constraintViolationException) {
    log.error("{} Request to {}, Invalid data received", request.getMethod(), request.getRequestURI(), constraintViolationException);

    String errorMessage = "Invalid data";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, constraintViolationException);

    constraintViolationException.getConstraintViolations()
            .forEach(violation -> apiError.addValidationError(violation.getPropertyPath().toString(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));

    return buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
    log.error("{} Request to {}, Invalid data received", servletRequest.getMethod(), servletRequest.getRequestURI(), ex);

    String errorMessage = "Invalid data";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, ex);
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> apiError.addValidationError(fieldError));
    ex.getBindingResult().getGlobalErrors().forEach(globalError -> apiError.addValidationError(globalError));

    return (ResponseEntity) buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
    log.error("{} Request to {} provided invalid JSON", servletRequest.getMethod(), servletRequest.getRequestURI(), ex);

    String errorMessage = "Invalid JSON format";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, ex);

    return (ResponseEntity) buildResponseEntity(apiError);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
    log.error("{} Request to {}, action not permitted by user, User={}, Authorities={}", 
              request.getMethod(), 
              request.getRequestURI(), 
              SecurityContextHolder.getContext().getAuthentication().getName(),
              SecurityContextHolder.getContext().getAuthentication().getAuthorities());

    String errorMessage = "Action not permitted";
    ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, errorMessage, ex);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException dataIntegrityViolationException) {
    log.error("{} Request to {}, Data integrity violation", request.getMethod(), request.getRequestURI(), dataIntegrityViolationException);

    String errorMessage = "Data integrity violation";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, dataIntegrityViolationException);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(TransientDataAccessResourceException.class)
  public ResponseEntity<ApiError> handleTransientDataAccessResourceException(HttpServletRequest request, TransientDataAccessResourceException transientDataAccessResourceException) {
    log.error("{} Request to {}, Data access failure", request.getMethod(), request.getRequestURI(), transientDataAccessResourceException);

    // We exclude the debugMessage from the ApiError as it can be too revealing of internal details
    String errorMessage = "Data access failure";
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException illegalArgumentException) {
    log.error("{} Request to {} provided an invalid request", request.getMethod(), request.getRequestURI(), illegalArgumentException);

    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, illegalArgumentException.getMessage(), illegalArgumentException);

    return buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
    log.error("{} Request to {} raised {}", servletRequest.getMethod(), servletRequest.getRequestURI(), ex, ex);

    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(HttpServletRequest request, Exception exception) {
    log.error("{} Request to {} raised {}", request.getMethod(), request.getRequestURI(), exception, exception);

    String errorMessage = "Something went wrong";
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, exception);

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(Error.class)
  public ResponseEntity<ApiError> handleGenericError(HttpServletRequest request, Error error) {
    log.error("{} Request to {} raised {}", request.getMethod(), request.getRequestURI(), error, error);

    String errorMessage = "Fatal internal server error occurred";
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, error);

    return buildResponseEntity(apiError);
  }
}
