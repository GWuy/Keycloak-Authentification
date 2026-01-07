package sp26.se194638.ojt.model.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  USERNAME_EXIST(HttpStatus.BAD_REQUEST),
  EMAIL_EXIST(HttpStatus.BAD_REQUEST),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
  FORBIDDEN(HttpStatus.FORBIDDEN),
  SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
  PASSWORD_DUPLICATED(HttpStatus.BAD_REQUEST),
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST),
  INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST),
  INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST);

  private final HttpStatus status;

  ErrorCode(HttpStatus status) {
    this.status = status;
  }

}
