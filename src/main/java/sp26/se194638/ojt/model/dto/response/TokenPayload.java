package sp26.se194638.ojt.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TokenPayload {
  private String token;
  private String jwtId;
  private Instant expiration;
}
