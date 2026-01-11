package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.dto.response.ProfileResponse;
import sp26.se194638.ojt.model.dto.response.UserLoggingResponse;
import sp26.se194638.ojt.service.AccountBanService;
import sp26.se194638.ojt.service.UserService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/assign1/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private AccountBanService accountBanService;

  @GetMapping("/me")
  public ProfileResponse userProfile(
    @RequestHeader(value = "Authorization", required = false) String header
  ) {
    return ResponseEntity.ok(userService.getProfile(header)).getBody();
  }

  @GetMapping("/online-users")
  public List<UserLoggingResponse> listOnlineUsers(
    @RequestHeader(value = "Authorization", required = false) String header
  ) {
    return userService.listOnlineUsers(header).getBody();
  }

  @PostMapping("/logout-user/{userId}")
  public ResponseEntity<?> logoutUser(
    @PathVariable Integer userId,
    @RequestHeader(value = "Authorization", required = false) String header
  ) {
    return ResponseEntity.ok(userService.adminLogoutUser(userId, header));
  }

}
