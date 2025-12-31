package sp26.se194638.ojt.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.mapper.AuditBacklogMapper;
import sp26.se194638.ojt.model.entity.AuditBacklog;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.response.AuditBacklogResponse;
import sp26.se194638.ojt.model.response.ErrorResponse;
import sp26.se194638.ojt.repository.AuditRepository;
import sp26.se194638.ojt.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuditService {
  @Autowired
  private AuditRepository auditRepository;

  @Autowired
  private AuditBacklogMapper auditBacklogMapper;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<?> listAll(String header) {

    String token = header.substring(7);
    if (token != null) {
      log.info("Da lay duoc token: {}", token);
    }
    else {
      log.info("Khong tim duoc token");
    }
    User admin = userRepository.findByUsername(jwtService.extractUsername(token));

    if (!admin.getRole().equalsIgnoreCase("ADMIN")) {
      ErrorResponse errorResponse = ErrorResponse.builder()
        .statusCode(403)
        .message("You don't have permission to access this action")
        .build();
      return ResponseEntity.ok(errorResponse);
    }

    List<AuditBacklog> auditBacklogs = auditRepository.findAll();
    List<AuditBacklogResponse> responses = new ArrayList<>();
    if (!auditBacklogs.isEmpty()){
      for (AuditBacklog auditBacklog : auditBacklogs) {
        AuditBacklogResponse toAudit = auditBacklogMapper.toAuditBacklogResponse(auditBacklog);
        responses.add(toAudit);
      }
    }
    return ResponseEntity.ok(responses);
  }

  public void addAudit(AuditBacklog auditBacklog) {

  }
}
