package sp26.se194638.ojt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sp26.se194638.ojt.service.AuditService;

@RestController
@RequestMapping("assign1/api/audit")
public class AuditBacklogController {
  @Autowired
  private AuditService auditService;

  @GetMapping
  public ResponseEntity<?> getAllAudit(@RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(auditService.listAll(token)).getBody();
  }
}
