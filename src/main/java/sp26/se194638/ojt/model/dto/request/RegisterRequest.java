package sp26.se194638.ojt.model.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String firstname;
    private String lastname;
    private LocalDate dayOfBirth;
}
