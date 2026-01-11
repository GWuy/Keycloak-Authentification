package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.dto.request.BanRequest;
import sp26.se194638.ojt.service.AccountBanService;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("assign1/api/ban-users")
public class AccountBanController {

  @Autowired
  private AccountBanService accountBanService;

  @GetMapping
  public ResponseEntity<?> listAllBannedAccounts(@RequestHeader("Authorization") String header) {
    return ResponseEntity.ok(accountBanService.listAllBannedUser(header));
  }

  @PostMapping("/ban/{userId}")
  public ResponseEntity<?> banAccount(
    @PathVariable Integer userId,
    @RequestBody BanRequest request,
    @RequestHeader(value = "Authorization", required = false) String header
  ) {
    return ResponseEntity.ok(accountBanService.banAccount(request, userId, header));
  }

  @PostMapping("/unban/{userId}")
  public ResponseEntity<?> unbanAccount(
    @PathVariable Integer userId,
    @RequestHeader(value = "Authorization", required = false) String header
  ) {
    return ResponseEntity.ok(accountBanService.unbanAccount(userId, header));
  }
}
