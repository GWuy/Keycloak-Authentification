package sp26.se194638.ojt.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sp26.se194638.ojt.annotation.Audit;
import sp26.se194638.ojt.exception.BusinessException;
import sp26.se194638.ojt.model.entity.AuditBacklog;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.utils.RequestContextUtil;
import sp26.se194638.ojt.repository.AuditRepository;
import sp26.se194638.ojt.service.CustomUserDetails;
import sp26.se194638.ojt.service.IpService;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

  @Autowired
  private AuditRepository auditRepository;

  @Around("@annotation(audit)")
  public Object audit(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
    User actor = null;
    String ipAddress = null;
    try {
      Object result = pjp.proceed();

      HttpServletRequest request = RequestContextUtil.getRequest();
      ipAddress = request != null
        ? IpService.getClientIp(request)
        : null;

      actor = null;
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated()
        && auth.getPrincipal() instanceof CustomUserDetails) {
        actor = ((CustomUserDetails) auth.getPrincipal()).getUser();
      }

      auditRepository.save(AuditBacklog.builder()
        .action(audit.action().name())
        .actor(actor)
        .status("SUCCESS")
        .actionAt(LocalDateTime.now())
        .ipAddress(ipAddress)
        .build());

      return result;
    } catch (BusinessException ex) {

      auditRepository.save(AuditBacklog.builder()
        .action(audit.action().name())
        .actor(actor)
        .status("FAILED")
        .errorReason(ex.getMessage())
        .actionAt(LocalDateTime.now())
        .ipAddress(ipAddress)
        .build());

      throw ex;
    }
  }
}
