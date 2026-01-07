package sp26.se194638.ojt.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class IpService {

  public static String getClientIp(HttpServletRequest request) {
    String[] headers = {
      "X-Forwarded-For",
      "X-Real-IP",
      "CF-Connecting-IP",
      "Forwarded"
    };

    for (String h : headers) {
      String ip = request.getHeader(h);
      if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
        return ip.split(",")[0].trim();
      }
    }
    return request.getRemoteAddr();
  }
}
