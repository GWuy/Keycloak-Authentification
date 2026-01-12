package sp26.se194638.ojt.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycRegisterRequest {
  private String no;
  private String fullName;
  private LocalDate dateOfBirth;
  private String sex;
  private String nationality;
  private String placeOfOrigin;
  private LocalDate dateOfExpiry;
  private LocalDate dateIssue;
  private String placeOfIssue;
}
