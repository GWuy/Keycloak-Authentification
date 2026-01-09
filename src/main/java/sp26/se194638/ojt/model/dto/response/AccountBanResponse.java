package sp26.se194638.ojt.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountBanResponse {
  private String banBy;
  private String reason;
  private boolean status;
  private LocalDateTime banAt;
}
