package sp26.se194638.ojt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.request.LoginRequest;
import sp26.se194638.ojt.model.request.RegisterRequest;
import sp26.se194638.ojt.model.response.LoginResponse;
import sp26.se194638.ojt.service.UserService;

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
    @RequestBody RegisterRequest request,
    HttpServletRequest servletRequest
  ) {
    return ResponseEntity.ok(userService.register(request, servletRequest)).getBody();
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken) {
    return ResponseEntity.ok(userService.refreshToken(refreshToken));
  }

}
