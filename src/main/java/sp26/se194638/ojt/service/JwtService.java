package sp26.se194638.ojt.service;

import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.model.dto.response.TokenPayload;
import sp26.se194638.ojt.model.dto.response.JwtResponse;


import javax.crypto.SecretKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-expiration}") // milliseconds
  private long accessExpiration;

  @Value("${jwt.refresh-expiration}") // milliseconds
  private long refreshExpiration;

  private SecretKey getKey() {
    byte[] keyBytes = Base64.getDecoder().decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public TokenPayload generateAccessToken(UserDetails userDetails) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessExpiration);
    String jwtId = UUID.randomUUID().toString();

    String token = Jwts.builder()
      .setId(jwtId)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(getKey(), SignatureAlgorithm.HS512)
      .compact();

    return TokenPayload.builder()
      .jwtId(jwtId)
      .token(token)
      .expiration(expiry.toInstant())
      .build();
  }


  public TokenPayload generateRefreshToken(UserDetails userDetails) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshExpiration);
    String jwtId = UUID.randomUUID().toString();

    String token = Jwts.builder()
      .setId(jwtId)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(getKey(), SignatureAlgorithm.HS512)
      .compact();

    return TokenPayload.builder()
      .jwtId(jwtId)
      .token(token)
      .expiration(expiry.toInstant())
      .build();
  }


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
      throw new RuntimeException("Invalid or expired JWT token: " + e.getMessage(), e);
    }
  }

  public JwtResponse parseToken(String token) throws ParseException {
    SignedJWT signedJWT = SignedJWT.parse(token);
    String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
    Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
    Date issuedAt = signedJWT.getJWTClaimsSet().getIssueTime();

    return JwtResponse.builder()
      .jwtId(jwtId)
      .issueTime(issuedAt)
      .expiration(expiration)
      .build();
  }

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

  public String extractJwId(String token) {
    return extractClaim(token, Claims::getId);
  }

}
