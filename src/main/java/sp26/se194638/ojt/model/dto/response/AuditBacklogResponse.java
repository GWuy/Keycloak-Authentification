package sp26.se194638.ojt.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditBacklogResponse {
  private String username;
  private String action;
  private String ipAddress;
  private int status;
  private LocalDateTime actionAt;
}
