package sp26.se194638.ojt.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sp26.se194638.ojt.exception.BusinessException;
import sp26.se194638.ojt.model.entity.AuditBacklog;
import sp26.se194638.ojt.model.entity.Blacklist;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.request.LoginRequest;
import sp26.se194638.ojt.model.request.RegisterRequest;
import sp26.se194638.ojt.model.response.LoginResponse;
import sp26.se194638.ojt.model.response.RegisterResponse;
import sp26.se194638.ojt.repository.AuditRepository;
import sp26.se194638.ojt.repository.BlacklistRepository;
import sp26.se194638.ojt.repository.UserRepository;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private IpService ipService;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();


    private static final String TOKEN_URL =
            "http://localhost:8080/realms/Customer/protocol/openid-connect/token";

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public ResponseEntity<LoginResponse> login(LoginRequest req, HttpServletRequest servletRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", req.getUsername());
        body.add("password", req.getPassword());

        try {
            ResponseEntity<String> kcResponse = restTemplate.postForEntity(
                    TOKEN_URL,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            if (kcResponse.getStatusCode().is2xxSuccessful()) {
                User userLogin = userRepository.findByUsername(req.getUsername());

                String accessToken = jwtService.generateAccessToken(userLogin);
                String refreshToken = jwtService.generateRefreshToken(userLogin);

                LoginResponse response = LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(LocalDateTime.now().plusDays(1))
                        .userId(userLogin.getId())
                        .roles(userLogin.getRole())
                        .build();

              AuditBacklog auditBacklog = AuditBacklog.builder()
                .user(userLogin)
                .action("User " + userLogin.getUsername() + " has logged in to the system")
                .ipAddress(ipService.getClientIp(servletRequest))
                .status(kcResponse.getStatusCode().value())
                .actionAt(LocalDateTime.now())
                .build();

              auditRepository.save(auditBacklog);

              return ResponseEntity.ok(response);
            }
            else {
              User userLogin = userRepository.findByUsername(req.getUsername());

              AuditBacklog auditBacklog = AuditBacklog.builder()
                .user(userLogin)
                .action("User " + userLogin.getUsername() + " has logged in to the system")
                .ipAddress(ipService.getClientIp(servletRequest))
                .status(kcResponse.getStatusCode().value())
                .actionAt(LocalDateTime.now())
                .build();

              auditRepository.save(auditBacklog);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account disabled");
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }

    public ResponseEntity<?> register(RegisterRequest request, HttpServletRequest servletRequest) {

        if (isUsernameExisted(request.getUsername())) {
            throw new BusinessException("Username has already existed");

        }

        if (isPasswordDuplicated(request.getPassword())) {
            throw new BusinessException("Password was duplicated");
        }

       if (!request.getPassword().matches(PASSWORD_REGEX)) {
           throw new BusinessException("Password must contain:\n" +
                   "- At least 8 characters\n" +
                   "- 1 uppercase letter\n" +
                   "- 1 lowercase letter\n" +
                   "- 1 number\n" +
                   "- 1 special character");
       }

        if (!request.getEmail().matches(EMAIL_REGEX)) {
            throw new BusinessException("Email doesn't email format");
        }

        if (isEmailExist(request.getEmail())) {
            throw new BusinessException("Email has already uses");
        }

        String firstname = request.getFirstname().trim();
        String lastname = request.getLastname().trim();

        User userRegister = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(firstname)
                .lastName(lastname)
                .password(request.getPassword())
                .status("ACTIVE")
                .role("USER")
                .active(1)
                .build();

        userRepository.save(userRegister);

        RegisterResponse response = RegisterResponse.builder()
                .message("Register new account successfully")
                .isSuccess(true)
                .build();

        return ResponseEntity.ok(response);
    }

    public boolean isPasswordDuplicated(String password) {
        return userRepository.existsByPassword(password);
    }

    public boolean isUsernameExisted(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailExist(String email) {
        List<String> emailList = userRepository.emails();

        for (int i = 0; i < emailList.size() - 1; i++) {
            if (emailList.get(i).equals(email)) {
                return true;
            }
        }
        return false;
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (blacklistRepository.existsByToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is blacklisted");
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or invalid");
        }

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        Blacklist blacklistEntry = Blacklist.builder()
                .token(refreshToken)
                .user(user)
                .reason("Used refresh token")
                .build();
        blacklistRepository.save(blacklistEntry);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(LocalDateTime.now().plusDays(1))
                .build();
    }
}
