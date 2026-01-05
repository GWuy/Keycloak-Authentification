package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.model.dto.request.FilterAuditRequest;
import sp26.se194638.ojt.service.AuditService;

@RestController
@RequestMapping("assign1/api/audit")
public class AuditBacklogController {
  @Autowired
  private AuditService auditService;

  @GetMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> getAllAudit(
    @RequestHeader("Authorization") String token,
    @RequestBody FilterAuditRequest filterAuditRequest) {
    return ResponseEntity.ok(auditService.listAll(token, filterAuditRequest)).getBody();
  }
}
