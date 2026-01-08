package sp26.se194638.ojt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sp26.se194638.ojt.model.dto.request.LoginRequest;
import sp26.se194638.ojt.model.dto.request.RegisterRequest;
import sp26.se194638.ojt.model.dto.response.LoginResponse;
import sp26.se194638.ojt.service.UserService;

import java.text.ParseException;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("assign1/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @PostMapping(
    value = "/login",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(userService.login(request));
  }

  @PostMapping(
    value = "/register",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> register(
    @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(userService.register(request));
  }

  @PostMapping("/refresh")
  public LoginResponse refresh(
    @RequestHeader(value = "Authorization", required = false) String header
  ) {
    if (header == null || !header.startsWith("Bearer ")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
    }
    String refreshToken = header.substring(7);
    return userService.refreshToken(refreshToken);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(
    @RequestHeader(value = "Authorization", required = false) String header) {
    return ResponseEntity.ok(userService.logout(header));
  }
}
