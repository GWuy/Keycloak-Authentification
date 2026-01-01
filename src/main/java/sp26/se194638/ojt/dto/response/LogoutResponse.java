package sp26.se194638.ojt.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
  private String message;
  private boolean isSuccess;
}
