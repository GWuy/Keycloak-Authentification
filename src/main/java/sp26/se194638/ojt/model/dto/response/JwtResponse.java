package sp26.se194638.ojt.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JwtResponse {
  private String jwtId;
  private Date issueTime;
  private Date expiration;
}
