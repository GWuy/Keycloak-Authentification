package sp26.se194638.ojt.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.model.dto.response.TokenPayload;
import sp26.se194638.ojt.model.dto.response.JwtResponse;


import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-expiration}")
  private long accessExpiration;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpiration;

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
  }

  public TokenPayload generateAccessToken(UserDetails userDetails) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessExpiration);
    String jti = UUID.randomUUID().toString();

    String token = Jwts.builder()
      .setId(jti)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(getKey(), SignatureAlgorithm.HS512)
      .compact();

    return TokenPayload.builder()
      .jwtId(jti)
      .token(token)
      .expiration(expiry.toInstant())
      .build();
  }

  public TokenPayload generateRefreshToken(UserDetails userDetails) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshExpiration);
    String jti = UUID.randomUUID().toString();

    String token = Jwts.builder()
      .setId(jti)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(getKey(), SignatureAlgorithm.HS512)
      .compact();

    return TokenPayload.builder()
      .jwtId(jti)
      .token(token)
      .expiration(expiry.toInstant())
      .build();
  }


  private Claims parseClaimsSafe(String token) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }

  public String extractUsername(String token) {
    Claims c = parseClaimsSafe(token);
    return c != null ? c.getSubject() : null;
  }

  public Date extractExpiration(String token) {
    Claims c = parseClaimsSafe(token);
    return c != null ? c.getExpiration() : null;
  }

  public String extractJwId(String token) {
    Claims c = parseClaimsSafe(token);
    return c != null ? c.getId() : null;
  }

  public boolean isAccessTokenValid(String token, UserDetails userDetails) {
    Claims c = parseClaimsSafe(token);
    return c != null
      && c.getSubject().equals(userDetails.getUsername())
      && c.getExpiration().after(new Date());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return isAccessTokenValid(token, userDetails);
  }

  public boolean isTokenExpired(String token) {
    Date exp = extractExpiration(token);
    return exp == null || exp.before(new Date());
  }

  public JwtResponse parseToken(String token) {
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(getKey())
      .build()
      .parseClaimsJws(token)
      .getBody();

    return JwtResponse.builder()
      .jwtId(claims.getId())
      .issueTime(claims.getIssuedAt())
      .expiration(claims.getExpiration())
      .build();
  }
}
