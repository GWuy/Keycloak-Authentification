package sp26.se194638.ojt.model.dto.response;

import lombok.Data;

@Data
public class UserLoggingResponse {
  private Integer id;
  private String username;
  private String fullName;
  private String email;
  private String role;
  private boolean isStatus;
}
