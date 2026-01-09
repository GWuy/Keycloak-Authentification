package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.exception.BusinessException;
import sp26.se194638.ojt.mapper.BanAccountMapper;
import sp26.se194638.ojt.model.dto.request.BanRequest;
import sp26.se194638.ojt.model.dto.response.AccountBanResponse;
import sp26.se194638.ojt.model.entity.AccountBan;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.enums.AuditAction;
import sp26.se194638.ojt.model.enums.ErrorCode;
import sp26.se194638.ojt.repository.AccountBanRepository;
import sp26.se194638.ojt.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class AccountBanService {

  @Autowired
  private BanAccountMapper banAccountMapper;

  @Autowired
  private AccountBanRepository accountBanRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtService jwtService;

  public AccountBanResponse banAccount(BanRequest banRequest, Integer userId, String header) {
    //lay header
    if (header == null || !header.startsWith("Bearer ")) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN, "Invalid token", AuditAction.BAN_ACCOUNT);
    }
    //cat header ra lay token
    String token = header.substring(7);
    //tim admin
    User admin = userRepository.findByUsername(jwtService.extractUsername(token));
    //kiem tra quyen
    if (admin == null || !admin.getRole().equalsIgnoreCase("ADMIN")) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "You don't have permission to access this resource", AuditAction.BAN_ACCOUNT);
    }

    User whoBanned = userRepository.findUserById(userId);
    if (whoBanned == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND, "User not found", AuditAction.BAN_ACCOUNT);
    }

    whoBanned.setStatus("INACTIVE");
    userRepository.save(whoBanned);

    //tao object
    AccountBan accountBan = AccountBan.builder()
      .user(whoBanned)
      .reason(banRequest.getReason().trim())
      .bannedBy(admin)
      .bannedAt(LocalDateTime.now())
      .build();
    //luu vao db
    accountBanRepository.save(accountBan);
    //mapper va trar ve
    return banAccountMapper.toAccountBanResponse(accountBan);
  }
}
