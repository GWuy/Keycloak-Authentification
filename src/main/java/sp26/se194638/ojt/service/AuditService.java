package sp26.se194638.ojt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.mapper.AuditBacklogMapper;
import sp26.se194638.ojt.model.dto.request.FilterAuditRequest;
import sp26.se194638.ojt.model.entity.AuditBacklog;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.dto.response.AuditBacklogResponse;
import sp26.se194638.ojt.model.dto.response.ErrorResponse;
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

  @Autowired
  private RedisService redisService;

  public ResponseEntity<?> listAll(String header, FilterAuditRequest filterAuditRequest) {

    //kieem tra xem cos phair la admin khoong
    if (!isAdmin(header)) {
      ErrorResponse errorResponse = ErrorResponse.builder()
        .statusCode(403)
        .message("You don't have permission")
        .build();
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(errorResponse);
    }

    String token = header.substring(7);
    if (!redisService.isTokenValid(jwtService.extractJwId(token), token)) {
      ErrorResponse errorResponse = ErrorResponse.builder()
        .statusCode(403)
        .message("Token is expired or invalid")
        .build();
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(errorResponse);
    }

    //list ra taast car nhuwngx backlog cos trong db
    List<AuditBacklog> auditBacklogs = auditRepository.findAll();
    //neeus admin khoong filter theo ngayf thif bor qua
    if (filterAuditRequest.getFromDate() != null && filterAuditRequest.getToDate() != null) {
      //filter theo ngayf
      auditBacklogs = auditBacklogs.stream().filter(
        audit -> !audit.getActionAt().isBefore(filterAuditRequest.getFromDate()) &&
          !audit.getActionAt().isAfter(filterAuditRequest.getToDate())
      ).toList();
    }

    //trar veef theo response
    List<AuditBacklogResponse> responses = new ArrayList<>();
    if (!auditBacklogs.isEmpty()){
      for (AuditBacklog auditBacklog : auditBacklogs) {
        AuditBacklogResponse toAudit = auditBacklogMapper.toAuditBacklogResponse(auditBacklog);
        responses.add(toAudit);
      }
    }
    return ResponseEntity.ok(responses);
  }

  private boolean isAdmin(String header) {
    String token = header.substring(7);
    log.info("Da lay duoc token: {}", token);
    User admin = userRepository.findByUsername(jwtService.extractUsername(token));

    if (admin.getRole().equalsIgnoreCase("ADMIN")) {
      return true;
    }
    return false;
  }

  public ResponseEntity<?> lockAccount(String header, Long id) {
    return null;
  }
}
