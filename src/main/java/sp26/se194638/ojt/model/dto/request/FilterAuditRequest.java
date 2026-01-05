package sp26.se194638.ojt.model.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilterAuditRequest {
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
}
