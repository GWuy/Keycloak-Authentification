package sp26.se194638.ojt.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresIn;
    private int userId;
    private String roles;
}
