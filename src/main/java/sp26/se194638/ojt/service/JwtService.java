package sp26.se194638.ojt.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-expiration}") // milliseconds
  private long accessExpiration;

  @Value("${jwt.refresh-expiration}") // milliseconds
  private long refreshExpiration;

  // --- SECRET KEY ---
  private SecretKey getKey() {
    // đảm bảo key dài tối thiểu 256 bit cho HS256
    byte[] keyBytes = secret.getBytes();
    if (keyBytes.length < 32) {
      throw new IllegalArgumentException("JWT secret key must be at least 256 bits / 32 bytes");
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // --- GENERATE TOKENS ---
  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(
      userDetails.getUsername(),
      userDetails.getAuthorities().iterator().next().getAuthority(),
      accessExpiration
    );
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(
      userDetails.getUsername(),
      null,
      refreshExpiration
    );
  }

  private String generateToken(String username, String role, long expirationMillis) {
    JwtBuilder builder = Jwts.builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
      .signWith(getKey(), SignatureAlgorithm.HS256);

    if (role != null) {
      builder.claim("role", role);
    }

    return builder.compact();
  }

  // --- EXTRACT INFO ---
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = parseClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (JwtException | IllegalArgumentException e) {
      // không ném RuntimeException thô ra, log rõ để debug filter
      throw new RuntimeException("Invalid or expired JWT token: " + e.getMessage(), e);
    }
  }

  // --- VALIDATION ---
  public boolean isAccessTokenValid(String token, UserDetails userDetails) {
    return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !extractExpiration(token).before(new Date());
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
