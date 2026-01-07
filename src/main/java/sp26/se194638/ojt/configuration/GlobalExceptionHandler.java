package sp26.se194638.ojt.configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sp26.se194638.ojt.exception.BusinessException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> handleBusiness(BusinessException ex) {
    return ResponseEntity
      .status(ex.getErrorCode().getStatus())
      .body(Map.of(
        "code", ex.getErrorCode().name(),
        "message", ex.getMessage()
      ));
  }
}
