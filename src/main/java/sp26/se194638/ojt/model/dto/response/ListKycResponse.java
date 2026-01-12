package sp26.se194638.ojt.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ListKycResponse {
  private String owner;
  private String idNumber;
  private String frontImage;
  private String backImage;
  private String status;
  private String submittedAt;
  private LocalDateTime verifiedAt;
}
