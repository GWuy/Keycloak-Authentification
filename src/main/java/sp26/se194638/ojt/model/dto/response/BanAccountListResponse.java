package sp26.se194638.ojt.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BanAccountListResponse {
  private Integer userId;
  private String userName;
  private String email;
  private String reason;
  private LocalDateTime banAt;
}
