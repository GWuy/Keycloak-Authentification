package sp26.se194638.ojt.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String message;
    private boolean isSuccess;
}
