package sp26.se194638.ojt.model.dto.request;

import lombok.Data;

@Data
public class OtpRequest {
  private String email;
  private String otp;
}
