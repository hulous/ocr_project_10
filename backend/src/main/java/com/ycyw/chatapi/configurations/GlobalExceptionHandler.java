package com.ycyw.chatapi.configurations;

import com.ycyw.chatapi.exceptions.ApiException;
import com.ycyw.chatapi.responses.ApiMessageResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final String DEFAULT_VALIDATION_MESSAGE = "Invalid request payload";

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<ApiMessageResponse> handleValidationException(Exception exception) {
    String validationErrors = extractValidationErrors(exception);
    String message = validationErrors.isBlank() ? DEFAULT_VALIDATION_MESSAGE : validationErrors;

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiMessageResponse().setMessage(message));
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiMessageResponse> handleApiException(ApiException exception) {
    return ResponseEntity.status(exception.getStatus())
        .body(new ApiMessageResponse().setMessage(exception.getMessage()));
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  public ResponseEntity<ApiMessageResponse> handleAuthenticationException(Exception exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiMessageResponse().setMessage("Invalid email or password"));
  }

  @ExceptionHandler(ClassCastException.class)
  public ResponseEntity<ApiMessageResponse> handleClassCastException(ClassCastException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiMessageResponse().setMessage("Unauthorized request"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiMessageResponse> handleUnexpectedException(Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiMessageResponse().setMessage("Internal server error"));
  }

  private String extractValidationErrors(Exception exception) {
    if (exception instanceof MethodArgumentNotValidException invalidException) {
      return invalidException.getBindingResult().getFieldErrors().stream()
          .map(FieldError::getDefaultMessage)
          .filter(message -> message != null && !message.isBlank())
          .distinct()
          .collect(Collectors.joining(", "));
    }

    if (exception instanceof BindException bindException) {
      return bindException.getBindingResult().getFieldErrors().stream()
          .map(FieldError::getDefaultMessage)
          .filter(message -> message != null && !message.isBlank())
          .distinct()
          .collect(Collectors.joining(", "));
    }

    return "";
  }
}
