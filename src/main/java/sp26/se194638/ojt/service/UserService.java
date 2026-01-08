package sp26.se194638.ojt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sp26.se194638.ojt.annotation.Audit;
import sp26.se194638.ojt.mapper.UserOnlineMapper;
import sp26.se194638.ojt.model.enums.AuditAction;
import sp26.se194638.ojt.model.enums.ErrorCode;
import sp26.se194638.ojt.exception.BusinessException;
import sp26.se194638.ojt.mapper.UserMapper;
import sp26.se194638.ojt.model.dto.response.*;
import sp26.se194638.ojt.model.entity.Blacklist;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.dto.request.LoginRequest;
import sp26.se194638.ojt.model.dto.request.RegisterRequest;
import sp26.se194638.ojt.repository.*;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private BlacklistRepository blacklistRepository;

  @Autowired
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

  @Autowired
  private UserOnlineMapper userOnlineMapper;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private RedisService redisService;

  @Value("${jwt.access-expiration}")
  private Long accessExpiration;

  @Value("${jwt.refresh-expiration}")
  private Long refreshExpiration;

  @Value("${keycloak.client-id}")
  private String clientId;

  @Value("${keycloak.client-secret}")
  private String clientSecret;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private final RestTemplate restTemplate = new RestTemplate();

  private static final String TOKEN_URL =
    "http://localhost:8080/realms/Customer/protocol/openid-connect/token";

  private static final String EMAIL_REGEX =
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

  private static final String PASSWORD_REGEX =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

  private static final String ONLINE_USER_KEY_PREFIX = "online:users:";

  @Audit(action = AuditAction.LOGIN)
  public LoginResponse login(LoginRequest req) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "password");
    body.add("client_id", clientId);
    body.add("client_secret", clientSecret);
    body.add("username", req.getUsername());
    body.add("password", req.getPassword());

    try {
      restTemplate.postForEntity(
        TOKEN_URL,
        new HttpEntity<>(body, headers),
        String.class
      );

      User user = userRepository.findByUsername(req.getUsername());
      if (user == null) {
        throw new BusinessException(
          ErrorCode.LOGINFAILED,
          "User not found",
          AuditAction.LOGIN
        );
      }

      TokenPayload accessToken = jwtService.generateAccessToken(user);
      TokenPayload refreshToken = jwtService.generateRefreshToken(user);

      redisService.saveToken(
        accessToken.getJwtId(),
        accessToken.getToken(),
        accessExpiration
      );

      // mark online
      redisTemplate.opsForValue().set(
        ONLINE_USER_KEY_PREFIX + user.getId(),
        "1",
        accessExpiration,
        TimeUnit.MILLISECONDS
      );

      return LoginResponse.builder()
        .accessToken(accessToken.getToken())
        .refreshToken(refreshToken.getToken())
        .expiresIn(LocalDateTime.now().plusSeconds(accessExpiration / 1000))
        .userId(user.getId())
        .roles(user.getRole())
        .build();

    } catch (HttpClientErrorException.Unauthorized ex) {
      throw new BusinessException(
        ErrorCode.LOGINFAILED,
        "Invalid username or password",
        AuditAction.LOGIN
      );
    } catch (HttpClientErrorException.Forbidden ex) {
      throw new BusinessException(
        ErrorCode.ACCOUNT_DISABLED,
        "Account disabled",
        AuditAction.LOGIN
      );
    }
  }

  @Audit(action = AuditAction.REGISTER)
  public ResponseEntity<?> register(RegisterRequest request) {

    if (isUsernameExisted(request.getUsername())) {
      throw new BusinessException(ErrorCode.USERNAME_EXIST ,"Username has already existed", AuditAction.REGISTER);

    }

    if (isPasswordDuplicated(request.getPassword())) {
      throw new BusinessException(ErrorCode.PASSWORD_DUPLICATED, "Password was duplicated", AuditAction.REGISTER);
    }

    if (!request.getPassword().matches(PASSWORD_REGEX)) {
      throw new BusinessException(
        ErrorCode.PASSWORD_NOT_MATCH,
        "Password must contain:\n" +
        "- At least 8 characters\n" +
        "- 1 uppercase letter\n" +
        "- 1 lowercase letter\n" +
        "- 1 number\n" +
        "- 1 special character",
        AuditAction.REGISTER);
    }

    if (!Objects.equals(request.getConfirmPassword(), request.getPassword())) {
      throw new BusinessException(
        ErrorCode.INVALID_PASSWORD_FORMAT,
        "Passwords don't match",
        AuditAction.REGISTER);
    }

    if (!request.getEmail().matches(EMAIL_REGEX)) {
      throw new BusinessException(
        ErrorCode.INVALID_EMAIL_FORMAT,
        "Email doesn't email format",
        AuditAction.REGISTER
        );
    }

    if (isEmailExist(request.getEmail())) {
      throw new BusinessException(
        ErrorCode.EMAIL_EXIST,
        "Email has already uses",
        AuditAction.REGISTER);
    }

    String firstname = request.getFirstname().trim();
    String lastname = request.getLastname().trim();

    User userRegister = User.builder()
      .username(request.getUsername())
      .email(request.getEmail())
      .firstName(firstname)
      .lastName(lastname)
      .password(passwordEncoder.encode(request.getPassword()))
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

  public boolean isPasswordDuplicated(String rawPassword) {
    return userRepository.findAll().stream()
      .anyMatch(u -> passwordEncoder.matches(rawPassword, u.getPassword()));
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

    if (jwtService.isTokenExpired(refreshToken)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
    }

    String username = jwtService.extractUsername(refreshToken);
    User user = userRepository.findByUsername(username);

    List<Blacklist> blacklists =
      blacklistRepository.findByUserId(user.getId());

    for (Blacklist b : blacklists) {
      if (passwordEncoder.matches(refreshToken, b.getTokenHash())) {
        throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "Refresh token is revoked"
        );
      }
    }

    TokenPayload newAccess = jwtService.generateAccessToken(user);
    TokenPayload newRefresh = jwtService.generateRefreshToken(user);


    blacklistRepository.save(
      Blacklist.builder()
        .user(user)
        .tokenHash(passwordEncoder.encode(refreshToken))
        .expiresAt(jwtService.extractExpiration(refreshToken))
        .reason("ROTATED")
        .build()
    );

    redisService.saveToken(
      newAccess.getJwtId(),
      newAccess.getToken(),
      accessExpiration
    );

    return LoginResponse.builder()
      .accessToken(newAccess.getToken())
      .refreshToken(newRefresh.getToken())
      .expiresIn(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
      .userId(user.getId())
      .roles(user.getRole())
      .build();
  }

  public ProfileResponse getProfile() {

    String username =
      SecurityContextHolder.getContext().getAuthentication().getName();

    User userProfile = userRepository.findByUsername(username);

    ProfileResponse response = userMapper.toProfileResponse(userProfile);
    return ResponseEntity.ok(response).getBody();
  }

  @Audit(action = AuditAction.LOGOUT)
  public ResponseEntity<?> logout(String header) {

    if (header == null || !header.startsWith("Bearer ")) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Missing Authorization header"
      );
    }

    String token = header.substring(7);

    String jwtId = jwtService.extractJwId(token);
    User user = userRepository.findByUsername(jwtService.extractUsername(token));

    redisService.revokeToken(jwtId);

    redisTemplate.delete(ONLINE_USER_KEY_PREFIX + user.getId());

    return ResponseEntity.ok(
      LogoutResponse.builder()
        .message("Logout successfully")
        .isSuccess(true)
        .build()
    );
  }

  @Audit(action = AuditAction.LIST_ONLINE_USER)
  public ResponseEntity<List<UserLoggingResponse>> listOnlineUsers(String header) {

    if (header == null || !header.startsWith("Bearer ")) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Missing Authorization header"
      );
    }

    String token = header.substring(7);

    if (!redisService.isTokenValid(
      jwtService.extractJwId(token),
      token
    )) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Token is expired or invalid"
      );
    }

    User admin =
      userRepository.findByUsername(jwtService.extractUsername(token));

    if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
      throw new BusinessException(
        ErrorCode.FORBIDDEN,
        "You don't have permission to access this resource",
        AuditAction.LIST_ONLINE_USER
      );
    }

    Set<String> keys = redisTemplate.keys(ONLINE_USER_KEY_PREFIX + "*");

    if (keys == null || keys.isEmpty()) {
      return ResponseEntity.ok(List.of());
    }

    List<Integer> userIds = keys.stream()
      .map(k -> Integer.parseInt(k.substring(ONLINE_USER_KEY_PREFIX.length())))
      .toList();

    log.info("List online users: {}", userIds.stream());

    List<User> users = userRepository.findAll();

    List<UserLoggingResponse> responses = users.stream()
      .map(userOnlineMapper::toOnUserLoggingResponse)
      .toList();

    for (Integer userId : userIds) {
      for (UserLoggingResponse response : responses) {
        if (response.getId() == userId.intValue()) {
          response.setStatus(true);
        }
      }
    }

    return ResponseEntity.ok(responses);
  }
}
