package sp26.se194638.ojt.model.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestContextUtil {

  private RequestContextUtil() {}

  public static HttpServletRequest getRequest() {
    ServletRequestAttributes attrs =
      (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attrs != null ? attrs.getRequest() : null;
  }
}
