package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.service.UserService;

@RestController
@RequestMapping("/assign1/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public ResponseEntity<?> userProfile(@RequestHeader("Authorization") String header) {
    return ResponseEntity.ok(userService.getProfile(header));
  }
}
