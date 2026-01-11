package sp26.se194638.ojt.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
  private Long id;
  private String url;
}
