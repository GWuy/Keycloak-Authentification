package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/assign1/api/overview")
public class OverviewController {

  @Autowired
  private UserService userService;

  @GetMapping
  public ResponseEntity<?> overview(@RequestHeader("Authorization") String header) {
    return ResponseEntity.ok(userService.overview(header));
  }
}
