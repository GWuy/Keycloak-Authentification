package sp26.se194638.ojt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.dto.request.LoginRequest;
import sp26.se194638.ojt.model.dto.request.RegisterRequest;
import sp26.se194638.ojt.model.dto.response.LoginResponse;
import sp26.se194638.ojt.service.UserService;

import java.text.ParseException;

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
  public ResponseEntity<?> login(
    @RequestBody LoginRequest request,
    HttpServletRequest servletRequest
  ) {
    return ResponseEntity.ok(userService.login(request, servletRequest)).getBody();
  }

  @PostMapping(
    value = "/register",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> register(
    @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(userService.register(request)).getBody();
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken) {
    return ResponseEntity.ok(userService.refreshToken(refreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> login(@RequestHeader("Authorization") String header) throws ParseException {
    return ResponseEntity.ok(userService.logout(header));
  }
}
