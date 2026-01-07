package sp26.se194638.ojt.exception;

import lombok.Getter;
import sp26.se194638.ojt.model.enums.AuditAction;
import sp26.se194638.ojt.model.enums.ErrorCode;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;
  private final AuditAction auditAction;

  public BusinessException(ErrorCode errorCode, String message, AuditAction auditAction) {
    super(message);
    this.errorCode = errorCode;
    this.auditAction = auditAction;
  }

}
