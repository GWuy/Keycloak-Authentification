package sp26.se194638.ojt.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UnbanAccountResponse {
  private String message;
  private String unbanBy;
  private LocalDateTime unbanAt;
}
