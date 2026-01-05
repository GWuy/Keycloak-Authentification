package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  public void saveToken(String jti, String token, long expiresInMs) {
    redisTemplate.opsForValue()
      .set("access_token:" + jti, token, expiresInMs, TimeUnit.MILLISECONDS);
  }

  public boolean isTokenValid(String jti, String token) {
    String redisToken = redisTemplate.opsForValue()
      .get("access_token:" + jti);
    return token.equals(redisToken);
  }

  public void revokeToken(String jti) {
    redisTemplate.delete("access_token:" + jti);
  }
}
