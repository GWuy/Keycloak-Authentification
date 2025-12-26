package sp26.se194638.ojt.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresIn;
}
