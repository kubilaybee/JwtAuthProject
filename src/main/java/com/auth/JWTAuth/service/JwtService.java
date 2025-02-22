package com.auth.JWTAuth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  @Value("${application.security.jwt.secret-key}")
  private String SECRET_KEY;

  @Value("${application.security.jwt.expiration}")
  private int JWT_EXPIRATION_DURATION;

  public String findUsername(String token) {
    return exportToken(token, Claims::getSubject);
  }

  private <T> T exportToken(java.lang.String token, Function<Claims, T> claimsTFunction) {
    final Claims claims =
        Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();

    return claimsTFunction.apply(claims);
  }

  private Key getKey() {
    byte[] key = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(key);
  }

  public boolean tokenControl(String jwt, UserDetails userDetails) {
    final String username = findUsername(jwt);
    return (username.equals(userDetails.getUsername())
        && !exportToken(jwt, Claims::getExpiration).before(new Date()));
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails);
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    var authorities =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_DURATION))
        .claim("authorities", authorities)
        .signWith(getKey())
        .compact();
  }
}
