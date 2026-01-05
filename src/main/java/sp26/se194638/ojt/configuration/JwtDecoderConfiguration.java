package sp26.se194638.ojt.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class JwtDecoderConfiguration implements JwtDecoder {

  @Value("${jwt.secret}")
  private String secretKey;

  private JwtDecoder delegate;

  @Override
  public Jwt decode(String token) throws JwtException {
    log.debug("Decoding token");
    if (delegate == null) {
      SecretKey key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
      delegate = NimbusJwtDecoder
        .withSecretKey(key)
        .macAlgorithm(MacAlgorithm.HS512)
        .build();
    }
    return delegate.decode(token);
  }
}
